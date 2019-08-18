package nl.frankkie.baxy2.retrofit

import nl.frankkie.baxy2.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConfig {

    private var gitHubRestApi: GithubRestApi? = null
    private var httpClient: OkHttpClient? = null


    private fun getHttpClient(): OkHttpClient? {
        if (httpClient == null) {
            synchronized(RetrofitConfig::class.java) {
                //prevent race-condition for singleton-creation
                if (httpClient == null) {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                    val builder = OkHttpClient.Builder()
                    if (BuildConfig.DEBUG) {
                        //only in debug, because will log http-body.
                        builder.addInterceptor(interceptor)
                    }
                    val client = builder.build()
                    //IdlingResource resource = OkHttp3IdlingResource.create("OkHttp", client);
                    //Espresso.registerIdlingResources(resource);
                    httpClient = client
                }
            }
        }
        return httpClient
    }

    fun getGithubApi(): GithubRestApi? {
        if (gitHubRestApi == null) {
            synchronized(RetrofitConfig::class.java) {
                //prevent race-condition for singleton-creation
                if (gitHubRestApi == null) {
                    val client = getHttpClient()
                    val retrofit = Retrofit.Builder()
                        .baseUrl(GithubRestApi.BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    gitHubRestApi = retrofit.create(GithubRestApi::class.java!!)
                }
            }
        }
        return gitHubRestApi
    }
}