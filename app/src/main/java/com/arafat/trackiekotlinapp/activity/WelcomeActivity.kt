package com.arafat.trackiekotlinapp.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.helper.SharedPrefManager
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    private lateinit var viewPager : ViewPager
    private lateinit var dotsLayout: LinearLayout
    private lateinit var dots: Array<TextView?>
    private val layouts: IntArray =  intArrayOf(
        R.layout.welcome_slide_one,
        R.layout.welcome_slide_two,
        R.layout.welcome_slide_three
    )

    lateinit var sharedPrefManager : SharedPrefManager

    private lateinit var myViewPagerAdapter: MyViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        initView()
        // making notification bar transparent
        changeStatusBarColor()

        btnLogIn.setOnClickListener {

            sharedPrefManager.setFirstTimeLaunch(false)
            val intent : Intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun initView() {

        dotsLayout = findViewById(R.id.layoutDots) as LinearLayout
        viewPager = findViewById(R.id.view_pager)

        sharedPrefManager = SharedPrefManager(this)

        addBottomDots(0)

        myViewPagerAdapter =
            MyViewPagerAdapter(this)
        viewPager.adapter = myViewPagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                addBottomDots(position)
            }

        })


    }

    private fun addBottomDots(currentPage: Int) {

        val dots: Array<TextView?> = arrayOfNulls(layouts.size)

        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_active)

        dotsLayout.removeAllViews()

        for (i: Int in 0..dots.size-1){

            dots[i] = TextView(this)
            dots[i]?.setText(Html.fromHtml("&#8226;"))
            dots[i]?.setTextSize(40f)
            dots[i]?.setTextColor(colorsInactive[currentPage])
            dotsLayout.addView(dots[i])
        }

        if (dots.size > 0)
            dots[currentPage]!!.setTextColor(colorsActive[currentPage])
    }



    class MyViewPagerAdapter(private val mContext : Context) : PagerAdapter(){

        private lateinit var layoutInflater: LayoutInflater

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            layoutInflater = LayoutInflater.from(mContext)

            val view : View = layoutInflater.inflate(WelcomeActivity().layouts[position],container,false)
            container.addView(view)
            return view;
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return WelcomeActivity().layouts.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}
