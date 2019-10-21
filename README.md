# SqlLite Example with live data and model view

## define user model in UserModel.kt

```

@Entity(tableName = "user")
class UserModel{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var Uid: Int = 0

    @ColumnInfo(name = "name")
    var Name: String = ""

    @ColumnInfo(name = "phone_number")
    var PhoneNumber: String = ""

    constructor(Name: String, PhoneNumber: String) {
        this.Name = Name
        this.PhoneNumber = PhoneNumber
    }
}

```

## define interface Dao for user in UserDao.kt

```

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<UserModel>>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<UserModel>>

    @Query("SELECT * FROM user WHERE name LIKE :nm LIMIT 1")
    fun findByName(nm: String): LiveData<UserModel>

    @Insert
    suspend fun insertAll(vararg users: UserModel)

    @Delete
    suspend fun delete(user: UserModel)
}


```


## define database connector in AppUserDatabase.kt

```


@Database(entities = arrayOf(UserModel::class), version = 1, exportSchema = false)
abstract class AppUserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    companion object{
        @Volatile
        private var INSTANCE : AppUserDatabase? =  null

        fun getDatabase(ctx : Context, scope: CoroutineScope) : AppUserDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return  tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    AppUserDatabase::class.java,
                    "user"
                ).addCallback(AppUserDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }

    private class AppUserDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    // add init data to database
                    // for testing
                    // but this currently not working

                    //populateDatabase(database.userDao())

                    // or just let this empty
                }
            }
        }
        suspend fun populateDatabase(userDao: UserDao) {
            userDao.insertAll(UserModel(Name = "reno",PhoneNumber = "08123113131"))
            userDao.insertAll(UserModel(Name = "reno",PhoneNumber = "08335343234"))
            userDao.insertAll(UserModel(Name = "reno",PhoneNumber = "08156564335"))
        }
    }
}

```


## define repository for user in UserRepository.kt

```

class UserRepository(private val userDao: UserDao) {

    // add more crud
    val allUser : LiveData<List<UserModel>> = userDao.getAll()


    suspend fun insert(user : UserModel){
        userDao.insertAll(user)
    }

    suspend fun delete(user : UserModel){
        userDao.delete(user)
    }
}


```

## define view model for user in UserViewModel.kt

```

class UserViewModel(aplication : Application) : AndroidViewModel(aplication){
    private val repository : UserRepository

    // add more crud
    val allUser : LiveData<List<UserModel>>

    init {
        val userDao = AppUserDatabase.getDatabase(aplication,viewModelScope).userDao()
        repository = UserRepository(userDao)

        // add more crud
        allUser = repository.allUser
    }


    fun insert(user : UserModel) = viewModelScope.launch {
        repository.insert(user)
    }

    fun delete(user : UserModel) = viewModelScope.launch {
        repository.delete(user)
    }
}

```

# how to use in activity


```

class MainActivity : AppCompatActivity() {

    lateinit var context : Context
    lateinit var adapterUser : AdapterUser
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWidget()
    }

    fun initWidget(){
        this.context = this@MainActivity

        setAdapter()

        userViewModel = ViewModelProvider(context as ViewModelStoreOwner).get(UserViewModel::class.java)
        userViewModel.allUser.observe(context as LifecycleOwner, Observer {
            it.let {
                adapterUser.setUsers(it)
            }
        })

        add_user.setOnClickListener {
            dialogInsert()
        }
    }

    fun setAdapter(){
        adapterUser = AdapterUser(context)
        adapterUser.setOnUserClick {
            dialogOpsi(it)
        }
        list_user.adapter = adapterUser
        list_user.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    fun dialogOpsi(item : UserModel){

        AlertDialog.Builder(context)
            .setTitle(item.Name)
            .setMessage("Number : ${item.Uid} Name : ${item.Name}\nPhone Number : ${item.PhoneNumber}")
            .setPositiveButton("Delete") { dialog, which ->
                userViewModel.delete(item)
                dialog.dismiss()
            }
            .setNegativeButton("Back") { dialog, which ->
                dialog.dismiss()
            }.create()
            .show()
    }

    fun dialogInsert(){

        val v = (context as Activity).layoutInflater.inflate(R.layout.dialog_add,null)

        val name : EditText = v.findViewById(R.id.add_user_name)
        val phone : EditText = v.findViewById(R.id.add_user_phone)

        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("Add") { dialog, which ->
                userViewModel.insert(UserModel(name.text.toString(),phone.text.toString()))
                dialog.dismiss()
            }
            .setNegativeButton("Close") { dialog, which ->
                dialog.dismiss()
            }.create()

        dialog.setView(v)
        dialog.setCancelable(false)
        dialog.show()
    }
}


```

