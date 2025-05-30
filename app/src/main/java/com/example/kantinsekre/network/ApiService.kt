import android.view.Menu
import com.example.kantinsekre.models.DetailTransaction
import com.example.kantinsekre.models.MenuResponse
import com.example.kantinsekre.models.User
import com.example.kantinsekre.models.Transaksi
import com.example.kantinsekre.models.UserResponse
import com.example.kantinsekre.models.createmenu
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("/user/login")
    suspend fun login(@Body request: User) : UserResponse

    @GET("/user/")
    suspend fun getAllUsers(): List<User>

    @GET("/user/{id}")
    suspend fun getUserById(@Path("id") id: String): User

    @POST("/add-user")
    suspend fun addUser(@Body user: User): Response<Unit>

    @PUT("/update-user/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): Response<Unit>

    @DELETE("/delete-user/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    @GET("/transaksi/")
    suspend fun getAllTransaksi(): List<Transaksi>

    @GET ("/transaksi/{id}")
    suspend fun  getTransaksiById(@Path("id") id: String): Transaksi

    @POST("/add-transaksi")
    suspend fun  addTransaksi(@Body transaksi: Transaksi): Response<Unit>

    @PUT("/update-transaksi/{id}")
    suspend fun updateTransaksi(@Path("id") id: String, @Body transaksi: Transaksi): Response<Unit>

    @DELETE("/delete-transaksi/{id}")
    suspend fun deletetransaksi(@Path("id") id: String): Response<Unit>

    @GET("/transaksi-detail/")
    suspend fun getAllTransaksiDetail(): List<DetailTransaction>

    @GET ("/transaksidetail/{id}")
    suspend fun  getTransaksiDetailById(@Path("id") id: String): DetailTransaction

    @POST("/add-transaksi-detail")
    suspend fun  addTransaksi(@Body transaksi: DetailTransaction): Response<Unit>

    @PUT("/update-transaksi/{id}")
    suspend fun updateTransaksi(@Path("id") id: String, @Body transaksi: DetailTransaction): Response<Unit>

    @DELETE("/delete-transaksi-detail/{id}")
    suspend fun deletetransaksiDetail(@Path("id") id: String): Response<Unit>

    @POST("/menu")
    suspend fun createMenu(@Body product: createmenu): Menu


    @GET("/menu")
    suspend fun getAllMenu(): MenuResponse

    @GET ("/menu/{id}")
    suspend fun  getMenuById(@Path("id") id: String): Menu

    @POST("/add-menu")
    suspend fun  addMenu(@Body menu: Menu): Response<Unit>

    @PUT("/update-menu/{id}")
    suspend fun updateMenu(@Path("id") id: String, @Body menu: Menu): Response<Unit>

    @DELETE("/delete-menu/{id}")
    suspend fun deleteMenu(@Path("id") id: String): Response<Unit>


}
