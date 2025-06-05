import android.view.Menu
import com.example.kantinsekre.models.AuthResponse
import com.example.kantinsekre.models.MenuResponse
import com.example.kantinsekre.models.User
import com.example.kantinsekre.models.Transaksi
import com.example.kantinsekre.models.DetailTransaction
import com.example.kantinsekre.models.TransaksiResponse
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
    suspend fun login(@Body request: User) : AuthResponse

    @GET("/user")
    suspend fun getAllUsers(): UserResponse

    @GET("/user/{id}")
    suspend fun getUserById(@Path("id") id: String): User

    @POST("/register")
    suspend fun addUser(@Body user: User): Response<Unit>

    @PUT("/update-user/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): Response<Unit>

    @DELETE("/delete-user/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    @GET("/transaksi")
    suspend fun getAllTransaksi(): TransaksiResponse

    @GET ("/transaksi/{id}")
    suspend fun  getTransaksiById(@Path("id") id: String): Transaksi

    @POST("transaksi/")
    suspend fun  addTransaksi(@Body transaksi: Transaksi): Response<Unit>

    @PUT("transaksi/{id}")
    suspend fun updateTransaksi(@Path("id") id: String, @Body transaksi: Transaksi): Response<Unit>

    @DELETE("transaksi/{id}")
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

    @GET("/menu")
    suspend fun getAllMenu(): MenuResponse

    @POST("menu/")
    suspend fun  addMenu(@Body request: createmenu): Response<Unit>

    @PUT("menu/{id}")
    suspend fun updateMenu(@Path("id") id: String, @Body menu: Menu): Response<Unit>

    @DELETE("menu/{id}")
    suspend fun deleteMenu(@Path("id") id: String): Response<Unit>


}
