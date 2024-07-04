package campus.tech.kakao.map

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import campus.tech.kakao.map.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    //private lateinit var dbHelper: DbHelper

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var savedSearchAdapter: SavedSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //dbHelper = DbHelper(this)

        //DB가 비어있을 때만 기본 데이터 추가
        /*
        if (dbHelper.isDBEmpty(dbHelper)) {
            insertInitialData(dbHelper)
        } */
        viewModel.insertInitialData()

        viewModel.searchResults.observe(this, Observer { results ->
            //binding.searchRecyclerView.adapter = SearchAdapter(results)
            searchAdapter.updateResults(results)
            binding.searchRecyclerView.visibility = if (results.isEmpty()) View.GONE else View.VISIBLE
            binding.noResult.visibility = if (results.isEmpty())View.VISIBLE else View.GONE
        })

        viewModel.savedSearches.observe(this, Observer { searches ->
            savedSearchAdapter.updateSearches(searches)
        })

        //X 버튼 클릭 시 입력창 초기화
        binding.buttonX.setOnClickListener {
            binding.inputSearch.text.clear()
        }

        binding.inputSearch.addTextChangedListener ( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if(query.isNotEmpty()) {
                    viewModel.searchDatabase(query)
                } else {
                    binding.searchRecyclerView.visibility = View.GONE
                    binding.noResult.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
            })

        //RecyclerView 설정
        //binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        setupRecyclerViews()
    }

    fun setupRecyclerViews() {
        searchAdapter = SearchAdapter { place ->
            viewModel.addSearch(place)
        }

        savedSearchAdapter = SavedSearchAdapter { place ->
            viewModel.removeSearch(place)
        }

        binding.savedSearchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchRecyclerView.adapter = searchAdapter

        binding.savedSearchRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.savedSearchRecyclerView.adapter = savedSearchAdapter
    }

}
