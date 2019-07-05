package com.example.luis.usersbrowser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.luis.usersbrowser.R
import com.example.luis.usersbrowser.retrofit.model.Result
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class UsersAdapter(private val context: Context, private var usersApiResponse: List<Result>?) : androidx.recyclerview.widget.RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val userClickSubject = PublishSubject.create<Result>()
    val userClickEvent: Observable<Result> = userClickSubject

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View
        val layoutInflater = LayoutInflater.from(context)
        view = layoutInflater.inflate(R.layout.item_grid, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)

        Glide.with(context)
                .load(usersApiResponse!![position].picture!!.thumbnail)
                .apply(requestOptions)
                .into(viewHolder.userThumbnail)

        viewHolder.userThumbnail.setOnClickListener {
            userClickSubject.onNext(usersApiResponse!![position])
        }
    }

    override fun getItemCount(): Int {
        return usersApiResponse!!.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        internal var userThumbnail: ImageView = itemView.findViewById(R.id.imgThumbnail)

    }
}
