package com.example.gitpulse

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ContributionAdapter(
    private val contributions: List<ContributionDay>
) : RecyclerView.Adapter<ContributionAdapter.ContributionViewHolder>() {

    class ContributionViewHolder(val cell: View) : RecyclerView.ViewHolder(cell)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contribution_cell, parent, false)
        return ContributionViewHolder(view)
    }

    override fun getItemCount() = contributions.size

    override fun onBindViewHolder(holder: ContributionViewHolder, position: Int) {
        // Use level (0-4) directly from GitHub's data-level attribute
        val color = when (contributions[position].level) {
            0 -> "#161B22"
            1 -> "#0E4429"
            2 -> "#006D32"
            3 -> "#26A641"
            else -> "#39D353"
        }
        holder.cell.setBackgroundColor(Color.parseColor(color))
    }
}