package com.janice.blogmapapp

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.article_template.view.*
import java.net.URL


class ArticleAdapter(val content: Content): RecyclerView.Adapter<CustomViewHolder>(){

    override fun getItemCount(): Int {
        return content.articles.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cell = layoutInflater.inflate(R.layout.article_template, parent,false)
        return CustomViewHolder(cell)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val article = content.articles.get(position)
        val authorName = "Author: " + article.author
        holder.itemView.author.text = authorName
        holder.itemView.title.text = article.title
        holder.itemView.description.text = article.description
        holder.itemView.date.text = article.article_date.dropLast(14)

        val articleImage = getG60xImageURL(article.image)
        DownLoadImageTask(holder.itemView.imageView).execute(articleImage)

        holder.itemView.setOnClickListener {
            val blog_fragment: BlogFragment = BlogFragment.newInstance()
            blog_fragment.showWebView(article.link.replace("http://", "https://"),
                holder.itemView.context as Activity)
        }
    }

    fun getG60xImageURL(url: String): String{
        val firstPart = url.substringBeforeLast("/")
        val middlePart = "/fit-in/60x/filters:autojpg()/"
        val lastPart = url.substringAfter(".com/")
        return firstPart + middlePart + lastPart
    }


    //The following code was obtained from: https://android--code.blogspot.com/2018/03/android-kotlin-imageview-set-image.html
    // Class to download an image from url and display it into an image view
    private class DownLoadImageTask(internal val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val urlOfImage = urls[0]
            return try {
                val inputStream = URL(urlOfImage).openStream()
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) { // Catch the download exception
                e.printStackTrace()
                null
            }
        }
        override fun onPostExecute(result: Bitmap?) {
            if(result!=null){
                // Display the downloaded image into image view
                imageView.setImageBitmap(result)
            }
        }
    }
}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view){}