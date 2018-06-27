package com.hellfish.evemento.event.comment

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.*
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import com.hellfish.evemento.api.Comment
import kotlinx.android.synthetic.main.comment_content.view.*
import java.util.*

class CommentAdapter(comments: MutableList<Comment>) : RecyclerAdapter<CardView, Comment>(comments) {

    override fun layout(item : Int): Int {
        return R.layout.comment_content
    }

    override fun doOnItemOnBindViewHolder(view: CardView, item: Comment, context: Context) {
        DrawableCompat.setTint(view.commentCircle.drawable, randomColor())

        view.commentInitial.text = item.name.first().toUpperCase().toString()

        view.commentUserName.text = item.name
        view.commentUserName.paintFlags = view.commentUserName.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        view.commentMessage.text = item.message
    }

    private fun randomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

}