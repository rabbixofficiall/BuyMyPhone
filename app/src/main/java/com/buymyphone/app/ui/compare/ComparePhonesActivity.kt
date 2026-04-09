package com.buymyphone.app.ui.compare

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityComparePhonesBinding
import com.buymyphone.app.storage.ReportHistoryManager

class ComparePhonesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComparePhonesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComparePhonesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reports = ReportHistoryManager.getReports(this)

        val titles = if (reports.isEmpty()) {
            listOf("No saved reports")
        } else {
            reports.map { it.title }
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            titles
        )

        binding.spinnerFirstPhone.adapter = adapter
        binding.spinnerSecondPhone.adapter = adapter

        binding.btnRunCompare.setOnClickListener {
            if (reports.size < 2) {
                binding.txtCompareResult.text = "At least two saved reports are needed."
                return@setOnClickListener
            }

            val firstIndex = binding.spinnerFirstPhone.selectedItemPosition
            val secondIndex = binding.spinnerSecondPhone.selectedItemPosition

            if (firstIndex == secondIndex) {
                binding.txtCompareResult.text = "Please select two different reports."
                return@setOnClickListener
            }

            val first = reports[firstIndex]
            val second = reports[secondIndex]

            binding.txtCompareResult.text = buildComparison(first.content, second.content, first.title, second.title)
        }

        binding.btnBackCompare.setOnClickListener {
            finish()
        }
    }

    private fun buildComparison(
        firstContent: String,
        secondContent: String,
        firstTitle: String,
        secondTitle: String
    ): String {
        val firstScore = extractOverallScore(firstContent)
        val secondScore = extractOverallScore(secondContent)

        val better = when {
            firstScore > secondScore -> firstTitle
            secondScore > firstScore -> secondTitle
            else -> "Both phones are equal by current overall score"
        }

        val diff = kotlin.math.abs(firstScore - secondScore)

        return buildString {
            appendLine("Phone 1: $firstTitle")
            appendLine("Overall Score: $firstScore")
            appendLine()
            appendLine("Phone 2: $secondTitle")
            appendLine("Overall Score: $secondScore")
            appendLine()
            appendLine("Score Difference: $diff")
            appendLine()
            appendLine("Better Overall: $better")
            appendLine()
            appendLine("Use this as a quick comparison. For full decision, compare camera, battery, and sensor sections too.")
        }
    }

    private fun extractOverallScore(content: String): Int {
        val regex = Regex("""Overall Score:\s*(\d+)""")
        val match = regex.find(content)
        return match?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
    }
}
