package com.example.luis.usersbrowser

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.luis.usersbrowser.adapter.FavoriteUsersAdapter
import com.example.luis.usersbrowser.adapter.SearchAdapter
import com.example.luis.usersbrowser.adapter.UsersAdapter
import com.example.luis.usersbrowser.db.UserEntity
import com.example.luis.usersbrowser.retrofit.model.Login
import com.example.luis.usersbrowser.retrofit.model.Name
import com.example.luis.usersbrowser.retrofit.model.Picture
import com.example.luis.usersbrowser.retrofit.model.Result
import com.example.luis.usersbrowser.retrofit.RetrofitClient
import com.example.luis.usersbrowser.retrofit.UsersApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity() {

    private lateinit var myAPI: UsersApi
    private var compositeDisposable = CompositeDisposable()
    private var currentPage = 1
    private val SEED = "seed"

    private var loading = true
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    private var results: ArrayList<Result> = ArrayList()
    private lateinit var favoriteUsers: MutableList<UserEntity>

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var favoriteUsersAdapter: FavoriteUsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        favoriteUsers = ArrayList()

        val recyclerView = recyclerView

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                //check for scroll down
                {
                    var layoutManager = recyclerView.layoutManager as androidx.recyclerview.widget.GridLayoutManager
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (loading) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            loading = false
                            currentPage++
                            loadUsers(currentPage)
                        }
                    }
                }
            }
        })

        //init API
        val retrofit = RetrofitClient.getInstance()
        myAPI = retrofit!!.create(UsersApi::class.java)

        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)

        loadUsers(currentPage)

        getFavoriteUsers()
    }

    private fun initFavUsersRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView = findViewById<RecyclerView>(com.example.luis.usersbrowser.R.id.recyclerViewFav)
        recyclerView.layoutManager = layoutManager
        favoriteUsersAdapter = FavoriteUsersAdapter(this, favoriteUsers)
        recyclerView.adapter = favoriteUsersAdapter
        subscribeToFavoriteSelection()
    }

    private fun subscribeToFavoriteSelection() {
        val userClickEvent = (favoriteUsersAdapter).favSelectedEvent.subscribe {
            navigateToUserDetailFragment(entityToResult(it))
        }
    }

    private fun entityToResult(it: UserEntity?): Result {
        val picture = Picture(it!!.picture_thumbnail, it.picture_large)
        val name = Name(it.name_first, it.name_last)
        val login = Login(it.username)
        return Result(picture, it.email, it.phone, name, login)
    }

    private fun getFavoriteUsers() {
        doAsync {
            favoriteUsers = UsersBrowserApplication.database.userDao().getAllUsers()
            uiThread {
                initFavUsersRecyclerView()
            }
        }
    }

    fun loadUsers(page: Int) {
        compositeDisposable.add(myAPI.getRates(page, 50, SEED)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ usersApiResponse ->
                    results.addAll(usersApiResponse.results!!)
                    loading = true
                    /*for some reason notifyDataSetChanged is not working for searchAdapter,
                    so I had to initialize it again to make it update*/
                    searchAdapter = SearchAdapter(this, results)
                    autoCompleteTextView.setAdapter(searchAdapter)
                    subscribeToSuggestionsSelection()
                    if (page == 1) {
                        usersAdapter = UsersAdapter(this, results)
                        recyclerView.adapter = usersAdapter
                        subscribeToUserSelection()
                    } else {
                        usersAdapter.notifyDataSetChanged()
                    }
                }, { throwable -> Toast.makeText(this@MainActivity, throwable.message, Toast.LENGTH_LONG).show() }))
    }

    private fun subscribeToSuggestionsSelection() {
        val subscribe = (searchAdapter).suggestionSelectedEvent.subscribe {
            autoCompleteTextView.setText("")
            navigateToUserDetailFragment(it)
        }

    }

    private fun subscribeToUserSelection() {
        var subscribe = (usersAdapter).userClickEvent
                .subscribe {
                    navigateToUserDetailFragment(it)
                }
    }

    private fun subscribeToFavoritedUser(userDetailFragment: UserDetailFragment) {
        var subscribe = (userDetailFragment).addFavEvent
                .subscribe {
                    favoriteUsers.add(it)
                    favoriteUsersAdapter.notifyDataSetChanged()
                }
    }

    private fun subscribeToUnfavoritedUser(userDetailFragment: UserDetailFragment) {
        var subscribe = (userDetailFragment).removeFavEvent
                .subscribe {
                    for (user in favoriteUsers) {
                        if (user.username == it) {
                            favoriteUsers.remove(user)
                            break
                        }
                    }
                    favoriteUsersAdapter.notifyDataSetChanged()
                }
    }

    private fun navigateToUserDetailFragment(result: Result) {
        val userDetailFragment = UserDetailFragment.newInstance(result)
        subscribeToFavoritedUser(userDetailFragment)
        subscribeToUnfavoritedUser(userDetailFragment)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.add(R.id.fragment_container, userDetailFragment).commit()
    }

    private fun addUser(user: UserEntity) {
        doAsync {
            val id = UsersBrowserApplication.database.userDao().addUser(user)
            uiThread {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

