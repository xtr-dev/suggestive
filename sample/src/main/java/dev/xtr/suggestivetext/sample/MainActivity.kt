package dev.xtr.suggestivetext.sample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
        Suggestive.recycler(query, adapter, onQuery = {
            vm.search(it)
        })
    }
}