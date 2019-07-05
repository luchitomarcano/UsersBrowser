package com.example.luis.usersbrowser

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.luis.usersbrowser.db.UserEntity
import com.example.luis.usersbrowser.retrofit.model.Result
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_user_detail.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class UserDetailFragment : Fragment() {

    private val addFavSubject = PublishSubject.create<UserEntity>()
    val addFavEvent: Observable<UserEntity> = addFavSubject
    private val removeFavSubject = PublishSubject.create<String>()
    val removeFavEvent: Observable<String> = removeFavSubject


    companion object {
        @JvmStatic
        fun newInstance(result: Result): UserDetailFragment {
            val fragment = UserDetailFragment()
            val bundle = Bundle()
            bundle.putSerializable("RESULT_KEY", result)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_detail, container, false)

        val result = arguments!!.getSerializable("RESULT_KEY") as Result

        var isUserFavorite = 0

        val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)

        Glide.with(this.activity!!)
                .load(result.picture!!.large)
                .apply(requestOptions)
                .into(view.findViewById(R.id.imgUserLarge))
        view.findViewById<TextView>(R.id.txtName).text = result.name!!.first + " " + result.name!!.last
        view.findViewById<TextView>(R.id.txtEmail).text = result.email
        view.findViewById<TextView>(R.id.txtPhone).text = result.phone

        doAsync {
            isUserFavorite = UsersBrowserApplication.database.userDao().isUserFavorite(result.login?.username!!)
            uiThread {
                view.checkBox.isChecked = isUserFavorite == 1
            }
        }

        view.checkBox.setOnClickListener {
            if (view.checkBox.isChecked) {
                isUserFavorite = 1
                doAsync {
                    var userEntity = UserEntity(username = result.login?.username!!, name_first = result.name!!.first!!, name_last = result.name!!.last!!, picture_thumbnail = result.picture!!.thumbnail!!, picture_large = result.picture!!.large!!, email = result.email!!, phone = result.phone!!)
                    val id = UsersBrowserApplication.database.userDao().addUser(userEntity)
                    uiThread {
                        addFavSubject.onNext(userEntity)

                    }
                }
            } else {
                isUserFavorite = 0
                doAsync {
                    val id = UsersBrowserApplication.database.userDao().deleteUser((result.login?.username!!))
                    uiThread {
                        removeFavSubject.onNext(result.login?.username!!)
                    }
                }
            }
        }

        view.txtAddToContacts.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.type = ContactsContract.Contacts.CONTENT_TYPE
            intent.putExtra(ContactsContract.Intents.Insert.NAME, result.name!!.first + " " + result.name!!.last)
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, result.phone)
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, result.email)
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                activity!!.startActivity(intent)
            }
        }

        return view
    }
}