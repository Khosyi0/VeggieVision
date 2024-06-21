package com.capstone.veggievision.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.veggievision.databinding.FragmentHomeBinding
import com.capstone.veggievision.ui.ViewModelFactory
import com.capstone.veggievision.ui.main.tutorial.TutorialActivity
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance()
        val viewModel: HomeViewModel by viewModels {
            factory
        }

        val newsAdapter = HomeAdapter()

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser!!.displayName
        val userFirst = user?.split(" ")!!.first()

        binding.apply {
            title.text = "Hello, $userFirst"
            progressBar.visibility = View.VISIBLE
            getStarted.setOnClickListener{
                startActivity(Intent(requireActivity(), TutorialActivity::class.java))
            }
        }

        binding.rvNews.apply {
            binding.progressBar.visibility = View.GONE
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = newsAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    newsAdapter.retry()
                }
            )
        }

        viewModel.getArticles().observe(viewLifecycleOwner) {
            newsAdapter.submitData(lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}