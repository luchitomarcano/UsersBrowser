package com.example.luis.usersbrowser.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.luis.usersbrowser.R
import com.example.luis.usersbrowser.db.UserEntity
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*


class FavoriteUsersAdapter(private val mContext: Context, favoriteUser: MutableList<UserEntity>) : RecyclerView.Adapter<FavoriteUsersAdapter.ViewHolder>() {

    private val favSelectedSubject = PublishSubject.create<UserEntity>()
    val favSelectedEvent: Observable<UserEntity> = favSelectedSubject

    //vars
    private var mFavoriteUser: MutableList<UserEntity> = ArrayList()

    init {
        mFavoriteUser = favoriteUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called.")

        Glide.with(mContext)
                .asBitmap()
                .load(mFavoriteUser[position].picture_thumbnail)
                .into(holder.image)

        holder.name.text = mFavoriteUser[position].name_first

        holder.image.setOnClickListener {
            Log.d(TAG, "onClick: clicked on an image: " + mFavoriteUser[position])
            favSelectedSubject.onNext(mFavoriteUser[position])
        }
    }

    override fun getItemCount(): Int {
        return mFavoriteUser.size
    }

    open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var image: CircleImageView = itemView.findViewById(R.id.image_view)
        internal var name: TextView = itemView.findViewById(R.id.name)

    }

    companion object {

        private const val TAG = "UsersAdapter"
    }
}