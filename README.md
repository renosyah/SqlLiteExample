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

    // only one primary constructor
    // added secondary constructor
    // will error the kotlin kapt
    constructor(Name: String, PhoneNumber: String) {
        this.Name = Name
        this.PhoneNumber = PhoneNumber
    }

    companion object {
        fun newUser(Uid: Int, Name: String, PhoneNumber: String) : UserModel {
            val u = UserModel(Name,PhoneNumber)
            u.Uid = Uid
            return u
        }
    }
}

```

## define interface Dao for user in UserDao.kt

```

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<UserModel>>

    @Query("SELECT * FROM user WHERE name LIKE :nm ")
    fun getAllByName(nm: String): LiveData<List<UserModel>>

    @Query("SELECT * FROM user WHERE uid = :id LIMIT 1")
    fun getOne(id : Int): LiveData<UserModel>

    @Insert
    suspend fun add(user: UserModel)

    @Update
    suspend fun update(user: UserModel)

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
            userDao.add(UserModel(Name = "reno",PhoneNumber = "08123113131"))
            userDao.add(UserModel(Name = "reno",PhoneNumber = "08335343234"))
            userDao.add(UserModel(Name = "reno",PhoneNumber = "08156564335"))
        }
    }
}


```


## define repository for user in UserRepository.kt

```

class UserRepository(private val userDao: UserDao) {


    fun getAll() : LiveData<List<UserModel>> {
       return userDao.getAll()
    }

    fun getAllByName(nm : String) : LiveData<List<UserModel>> {
        return userDao.getAllByName(nm)
    }

    fun getOne(id : Int) : LiveData<UserModel> {
        return userDao.getOne(id)
    }

    suspend fun add(user : UserModel){
        userDao.add(user)
    }

    suspend fun delete(user : UserModel){
        userDao.delete(user)
    }

    suspend fun update(user : UserModel){
        userDao.update(user)
    }
}

```

## define view model for user in UserViewModel.kt

```

class UserViewModel(aplication : Application) : AndroidViewModel(aplication){
    private val repository : UserRepository

    init {
        val userDao = AppUserDatabase.getDatabase(aplication,viewModelScope).userDao()
        repository = UserRepository(userDao)
    }

    fun getAllUser() : LiveData<List<UserModel>>{
        return repository.getAll()
    }
    fun getAllByName(nm : String) : LiveData<List<UserModel>>{
        return repository.getAllByName(nm)
    }
    fun getOne(id : Int) : LiveData<UserModel>{
        return repository.getOne(id)
    }
    fun add(user : UserModel) = viewModelScope.launch {
        repository.add(user)
    }
    fun delete(user : UserModel) = viewModelScope.launch {
        repository.delete(user)
    }
    fun update(user : UserModel) = viewModelScope.launch {
        repository.update(user)
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
        userViewModel.getAllUser().observe(context as LifecycleOwner, Observer {
            it.let {
                adapterUser.setUsers(it)
            }
        })

        add_user.setOnClickListener(onAddUser)
        find_user.addTextChangedListener(onFindUser)
    }

    val onAddUser = object : View.OnClickListener {
        override fun onClick(v: View?) {
            dialogInsert()
        }
    }

    val onFindUser = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            userViewModel.getAllByName("%${s.toString()}%").observe(context as LifecycleOwner, Observer {
                it.let {
                    adapterUser.setUsers(it)
                }
            })
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
            .setPositiveButton("Edit") { dialog, which ->
                dialogEdit(item)
                dialog.dismiss()
            }
            .setNeutralButton("Delete") { dialog, which ->
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

        val title : TextView = v.findViewById(R.id.title)
        title.setText("Add New User")

        val name : EditText = v.findViewById(R.id.add_user_name)
        val phone : EditText = v.findViewById(R.id.add_user_phone)

        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("Add") { dialog, which ->
                userViewModel.add(UserModel(name.text.toString(),phone.text.toString()))
                dialog.dismiss()
            }
            .setNegativeButton("Close") { dialog, which ->
                dialog.dismiss()
            }.create()

        dialog.setView(v)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun dialogEdit(item : UserModel) {
        val v = (context as Activity).layoutInflater.inflate(R.layout.dialog_add,null)

        val title : TextView = v.findViewById(R.id.title)
        title.setText("Edit User : ${item.Name}")

        val name : EditText = v.findViewById(R.id.add_user_name)
        name.setText(item.Name)

        val phone : EditText = v.findViewById(R.id.add_user_phone)
        phone.setText(item.PhoneNumber)

        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("Update") { dialog, which ->
                userViewModel.update(UserModel.newUser(item.Uid,name.text.toString(),phone.text.toString()))
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

