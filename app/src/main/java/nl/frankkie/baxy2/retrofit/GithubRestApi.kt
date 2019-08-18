package nl.frankkie.baxy2.retrofit

import nl.frankkie.baxy2.retrofit.model.GithubApiResponse
import retrofit2.Call
import retrofit2.http.GET

interface GithubRestApi {
    companion object {
        public val BASE_URL = "https://github.com/frankkienl/"
    }

    @GET("")
    abstract fun checkLatestVersion(): Call<GithubApiResponse>
}