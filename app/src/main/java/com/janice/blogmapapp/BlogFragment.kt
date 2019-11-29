package com.janice.blogmapapp


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BlogFragment : Fragment(){

    lateinit var mRecyclerView : RecyclerView

    companion object {
        fun newInstance(): BlogFragment {
            val fragment = BlogFragment()
            return BlogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_blog, container, false)
        mRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView) as RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    fun setUpRecyclerView(content: Content, context_ : Activity){
        mRecyclerView = context_.findViewById<RecyclerView>(R.id.recyclerView) as RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(context_)
        mRecyclerView.adapter = ArticleAdapter(content)
    }

    fun showWebView(url: String, context_ : Activity){
        val webView = context_.findViewById<WebView>(R.id.webView)
        val closeWebViewButton = context_.findViewById<ImageButton>(R.id.closeWebViewButton)
        webView.visibility = View.VISIBLE
        webView.loadUrl(url)
        closeWebViewButton.visibility = View.VISIBLE
        closeWebViewButton.setOnClickListener {
            webView.visibility = View.INVISIBLE
            closeWebViewButton.visibility = View.INVISIBLE
        }
    }
}
