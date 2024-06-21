package com.capstone.veggievision.ui.main.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.veggievision.data.remote.response.ItemHistory
import com.capstone.veggievision.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyHistory: TextView
    private lateinit var historyAdapter: HistoryAdapter
    private val historyList: MutableList<ItemHistory> = mutableListOf()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.rvItemScan
        emptyHistory = binding.emptyHistory

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter(historyList)
        recyclerView.adapter = historyAdapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadHistoryFromFirebase()
    }

    private fun loadHistoryFromFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser!!.displayName
        val database = FirebaseDatabase.getInstance()
        val historyRef = database.getReference("users").child(userId.toString()).child("history")

        historyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historyList.clear()
                for (historySnapshot in snapshot.children) {
                    val title = historySnapshot.child("title").getValue(String::class.java)
                    val image = historySnapshot.child("image").getValue(String::class.java)
                    val progress = historySnapshot.child("progress").getValue(String::class.java)
                    val timestamp = historySnapshot.child("timestamp").getValue(Long::class.java)
                    val desc = historySnapshot.child("desc").getValue(String::class.java)

                    if (progress != null && timestamp != null && title != null && image != null && desc != null) {
                        historyList.add(ItemHistory(title, image, progress, timestamp, desc))
                    }
                }

                if (historyList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyHistory.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyHistory.visibility = View.GONE
                }

                historyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("HistoryFragment", "loadHistoryFromFirebase:onCancelled", error.toException())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}