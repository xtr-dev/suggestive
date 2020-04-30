package dev.xtr.suggestivetext.sample

import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import dev.xtr.suggestive.SuggestionWindow
import dev.xtr.suggestive.Suggestive
import dev.xtr.suggestivetext.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val vm by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MainAdapter()
        vm.results.observe(this, Observer {
            adapter.submitList(it)
        })

        val background = ContextCompat.getDrawable(this, R.drawable.popup_rounded_bg)
            ?: return
        Suggestive.recycler(
            // anchor to queryEditText
            queryEditText,
            // use the rv adapter
            adapter,
            // only invoke onQuery at a minimum input of 3 characters
            minCharacters = 3,
            // attaches the text change listener that invokes [onQuery]
            attachTextChangeListener = true,
            // invoke onQuery with a minimum delay of 200 ms
            onQueryThrottle = 200,
            // align the popup to the start of the anchor view
            gravity = Gravity.START,
            // use the background drawable for the popup window background
            backgroundDrawable = background,
            // constrains the popup windows width to the width of anchor
            constrainWidthToAnchorBounds = true,
            // hide the popup window when anchor loses focus
            hideOnBlur = true,
            // show the popup above or below anchor based on available space around anchor
            preferredPosition = SuggestionWindow.PreferredPosition.BEST_FIT,
            // the input query callback, called for every text change event on anchor
            //  (with exclusions as mandated by the minCharacters and onQueryThrottle options)
            onQuery = { query ->
            vm.search(query)
        })

    }
}