package com.example.bonsaicare.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bonsaicare.R
import com.example.bonsaicare.ui.database.Task


class CardListTaskTypeAdapter(private val sharedBonsaiViewModel: BonsaiViewModel) : ListAdapter<CardTaskType, CardListTaskTypeAdapter.CardViewHolder>(CARDS_TASK_COMPARATOR) {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Reference to textViewTreeName in dynamic_card_item.xml is created
        val title: TextView = itemView.findViewById(R.id.card_title)
        val titleLatin: TextView = itemView.findViewById(R.id.card_title_latin)
        val titleHardinessZone: TextView = itemView.findViewById(R.id.hardiness_zone)

        // Get reference to table_layout_calendar
        val tableLayoutCalendar: TableLayout = itemView.findViewById(R.id.table_layout_calendar)

        // Get reference to linearLayoutCalendar
        val linearLayoutCalendar: LinearLayout = itemView.findViewById(R.id.linear_layout_calendar)

        // Get reference to header task textViewTreeName
        private val textViewHeaderTask: TextView = itemView.findViewById(R.id.header_task)

        fun bind(taskType: String?, hardinessZone: String?) {
            // Set text for title and titleLatin
            title.text = taskType
            // Get description string from string xml
            titleLatin.text = itemView.context.getString(R.string.description)
            titleHardinessZone.text = hardinessZone
            // Set text of header task (from Task) to Tree
            textViewHeaderTask.text = itemView.context.getString(R.string.tree_species_title)
        }

        companion object {
            fun create(parent: ViewGroup): CardViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_card_item, parent, false)
                return CardViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Get current Item by position
        val currentCard = getItem(position)

        // Get list of tasks from currentCard
        val currentListOfTasks: MutableList<Task> = currentCard.listOfTasks

        // Get reference to tableLayoutCalendarContext
        val tableLayoutCalendarContext = holder.tableLayoutCalendar.context

        // Set onClickListener for textViewTreeName task type title to unfold (visible) / fold (gone) calendar card
        holder.title.setOnClickListener {
            // If not visible (folded) -> make visible (unfold)
            if (holder.linearLayoutCalendar.visibility == View.GONE) {

                // Create cards only if child count is 1
                if (holder.tableLayoutCalendar.childCount == 1) {

                    // Loop over list of tasks and create all textViews (task name and up to 12 months)
                    for (currentTask in currentListOfTasks) {
                        // Set row for current task
                        val tr1 = TableRow(tableLayoutCalendarContext)

                        val tableRowParams = TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT
                        )
                        val leftMargin = 0
                        val topMargin = 2
                        val rightMargin = 0
                        val bottomMargin = 2
                        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
                        tr1.layoutParams = tableRowParams

                        // Add table row to tableLayoutCalendar
                        holder.tableLayoutCalendar.addView(tr1)

                        // Create and add textViews for taskName and short description for month
                        val txTaskName = TextView(tableLayoutCalendarContext)
                        val txVwStrokeBorder = TextView(tableLayoutCalendarContext)
                        val txJan = TextView(tableLayoutCalendarContext)
                        val txFeb = TextView(tableLayoutCalendarContext)
                        val txMar = TextView(tableLayoutCalendarContext)
                        val txApr = TextView(tableLayoutCalendarContext)
                        val txMay = TextView(tableLayoutCalendarContext)
                        val txJun = TextView(tableLayoutCalendarContext)
                        val txJul = TextView(tableLayoutCalendarContext)
                        val txAug = TextView(tableLayoutCalendarContext)
                        val txSep = TextView(tableLayoutCalendarContext)
                        val txOct = TextView(tableLayoutCalendarContext)
                        val txNov = TextView(tableLayoutCalendarContext)
                        val txDec = TextView(tableLayoutCalendarContext)

                        // Add text views to row
                        tr1.addView(txTaskName)
                        tr1.addView(txVwStrokeBorder)
                        tr1.addView(txJan)
                        tr1.addView(txFeb)
                        tr1.addView(txMar)
                        tr1.addView(txApr)
                        tr1.addView(txMay)
                        tr1.addView(txJun)
                        tr1.addView(txJul)
                        tr1.addView(txAug)
                        tr1.addView(txSep)
                        tr1.addView(txOct)
                        tr1.addView(txNov)
                        tr1.addView(txDec)

                        // Set up text views for taskName and short descriptions
                        setUpTextViewTaskName(tableLayoutCalendarContext, txTaskName, currentTask)
                        setUpTextViewsMonths(txJan, 1, currentTask)
                        setUpTextViewsMonths(txFeb, 2, currentTask)
                        setUpTextViewsMonths(txMar, 3, currentTask)
                        setUpTextViewsMonths(txApr, 4, currentTask)
                        setUpTextViewsMonths(txMay, 5, currentTask)
                        setUpTextViewsMonths(txJun, 6, currentTask)
                        setUpTextViewsMonths(txJul, 7, currentTask)
                        setUpTextViewsMonths(txAug, 8, currentTask)
                        setUpTextViewsMonths(txSep, 9, currentTask)
                        setUpTextViewsMonths(txOct, 10, currentTask)
                        setUpTextViewsMonths(txNov, 11, currentTask)
                        setUpTextViewsMonths(txDec, 12, currentTask)
                    }
                }
                // Make layout visible
                holder.linearLayoutCalendar.visibility = View.VISIBLE
            }
            else  {
                // If visible (unfolded) -> delete text views and make in-visible (fold)
                holder.linearLayoutCalendar.visibility = View.GONE
            }
        }

        // Set onClickListener for textViewTreeName [i] to show a detailed description of the task
        holder.titleLatin.setOnClickListener {
            val dialogBuilderStartUp = AlertDialog.Builder(tableLayoutCalendarContext)

            // Todo MVP3: All that gets obsolete once we access string.xml in BonsaiDB.kt
            var taskDescription = "Detailed task descriptions will follow."
            when (currentCard.taskType.name) {
                "Repotting" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.repotting_description)
                }
                "Location" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.location_description)
                }
                "Pinching" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.pinching_description)
                }
                "Pruning" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.pruning_description)
                }
                "Defoliation" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.defoliation_description)
                }
                "Wiring" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.wiring_description)
                }
                "Fertilizing" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.fertilizing_description)
                }
                "Watering" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.watering_description)
                }
                "Propagation" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.propagation_description)
                }
                "Diseases" -> {
                    taskDescription = HtmlCompat.fromHtml(tableLayoutCalendarContext.resources.getString(R.string.diseases_description), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                }
                "Specifics" -> {
                    taskDescription = tableLayoutCalendarContext.resources.getString(R.string.specifics_description)
                }
            }

            // Setup Dialog box for taskDescription
            dialogBuilderStartUp.setMessage(taskDescription)
                // If the dialog is cancelable
                .setCancelable(false)
                // Negative button text and action
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.cancel()
                }

            // Create dialog box and show alert
            val alert = dialogBuilderStartUp.create()
            // Set title for alert dialog box
            alert.setTitle(currentCard.taskType.name)
            // Show alert dialog
            alert.show()
        }

        // Set onClickListener for Hardiness Zones TextView to show a short description and weblink
        holder.titleHardinessZone.setOnClickListener {
            val dialogBuilderStartUpHardiness = AlertDialog.Builder(tableLayoutCalendarContext)

            dialogBuilderStartUpHardiness
                .setCancelable(false)
                // Negative button text and action
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.cancel()
                }
                .setMessage(R.string.hardiness_zones_and_link)
                .setTitle("Hardiness Zone")
                .show()
                .apply {
                    findViewById<TextView>(android.R.id.message)
                        ?.movementMethod = LinkMovementMethod.getInstance()
                }
        }

        // Bind information to holder
        holder.bind(currentCard.taskType.name, currentCard.hardinessZone)
    }

    companion object {
        private val CARDS_TASK_COMPARATOR = object : DiffUtil.ItemCallback<CardTaskType>() {
            override fun areItemsTheSame(oldItem: CardTaskType, newItem: CardTaskType): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: CardTaskType, newItem: CardTaskType): Boolean {
                return oldItem.cardName == newItem.cardName
            }
        }
    }

    // Function to set up text view for dynamic table row (month: 1-12)
    // This function sets text views in a table row according to the passed task
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ResourceType")
    private fun setUpTextViewsMonths(textView: TextView, inputMonthInt: Int, currentTask: Task): TextView {

        // Get tasks list of dates as String and convert to List of Int
        val tasksListOfDatesAsString = currentTask.listOfDates
        val tasksListOfDatesAsListOfString = tasksListOfDatesAsString.split(",").toTypedArray()
        val tasksListOfDates = tasksListOfDatesAsListOfString.map { it.trim().toInt() }.toTypedArray()

        // Get tasks list of labels (as a string) and convert to List of String
        val tasksListOfLabelsAsString = currentTask.listOfLabels
        val tasksListOfLabels = tasksListOfLabelsAsString.split(",").map { it.trim() }

        // Init Boolean for month present
        var monthPresent = false
        var rangeIndex = 0

        // Check if task is present in this month
        // e.g. [2, 3, 11, 1] -> Feb-Mar, Nov-Jan

        // Set manual iterator for upcoming for loop
        var manualIter = 0

        // Set descriptionIncrement to count set descriptions
        var descriptionIncrement = 0

        // Loop over every (second) month in tasksListOfDates (e.g.: [2, 3, 10, 1] or [2, 2, 4, 7, 11, 12])
        for (currentMonth in tasksListOfDates) {

            // Skip iteration if manualIter is not even
            if (manualIter % 2 == 1) {   // only 2 and 10 are looped
                // Increment manualIter
                manualIter += 1

                // Continue with next iteration
                continue
            }

            // If start month > end month
            if (tasksListOfDates[manualIter] > tasksListOfDates[manualIter+1]) {   // 2 > 2 = false,   10 > 1 = true
                // Get min and max range from tasksListOfDates
                val minRange = tasksListOfDates[manualIter+1]+1  // 2+1=3;    [11, 2]
                val maxRange = tasksListOfDates[manualIter]-1  // 11-1=10

                // Check if inputMonthInt is NOT in range of taskListOfDates
                if ( !(minRange .. maxRange).contains(inputMonthInt) ) {  // !(3..10.contains(11)) = true: 10-3=true
                    monthPresent = true
                    // Set rangeIndex (this variable is to find corresponding Description String in ListOfLabels
                    rangeIndex = inputMonthInt - tasksListOfDates[manualIter] + descriptionIncrement // 11-11=0, 11:1, 3-10=-7+12=5, 3-(3)=0, 4-(3)=1, 10-3=7  range(0,5)

                    // if range index is negative, add 12 "month"
                    if (rangeIndex<0) {
                        rangeIndex+=12
                    }
                }
            }
            // If inputMonthInt is within dates range -> set to True
            else if ((tasksListOfDates[manualIter]..tasksListOfDates[manualIter+1]).contains(inputMonthInt)) {     // e.g. (2..2).contains(4) false
                monthPresent = true
                rangeIndex = inputMonthInt - tasksListOfDates[manualIter] + descriptionIncrement // rangeIndex = 4(April) - tasks..[0]=>2 = 2
            }
            else {
                // Increment value according to number of months in subtask - e.g. [2, 2, 4, 7, 11, 12]   7-4+1=4
                descriptionIncrement += tasksListOfDates[manualIter+1] - tasksListOfDates[manualIter] + 1
            }

            // Increment manualIter
            manualIter += 1
        }

        if (monthPresent) {
            // Set textViewTreeName text from list of labels if string is not "xxx"
            // If rangeIndex greater size of tasksListOfLabels -> set textView.text to ""
            if (tasksListOfLabels.size <= rangeIndex) {
                textView.text = ""
            }
            else {
                // Set text to "" if "xxx" or to actual label if provided
                if (tasksListOfLabels[rangeIndex].contains("xxx")) {
                    textView.text = ""
                } else {
                    textView.text = tasksListOfLabels[rangeIndex]
                }
            }

            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15F) // text size 15dp
            textView.setPadding(0, 8, 0, 0) // padding 8
            textView.gravity = Gravity.CENTER_HORIZONTAL  // gravity center_horizontal|center_vertical
            // This overwrote the text color
            textView.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium) // textAppearance "?attr/textAppearanceHeadline6"
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.black))
            textView.setBackgroundResource(R.color.repotting)

            // Set text color and background color by task type
            when (currentTask.taskType.name) {
                "Repotting" -> textView.setBackgroundResource(R.color.repotting)
                "Location" -> textView.setBackgroundResource(R.color.location)
                "Pinching" -> textView.setBackgroundResource(R.color.pinching)
                "Pinching (ph1)" -> textView.setBackgroundResource(R.color.pinching)
                "Pinching (ph2)" -> textView.setBackgroundResource(R.color.pinching)
                "Pruning" -> textView.setBackgroundResource(R.color.pruning)
                "Defoliation" -> textView.setBackgroundResource(R.color.defoliation)
                "Defoliation (ph1)" -> textView.setBackgroundResource(R.color.defoliation)
                "Defoliation (ph2)" -> textView.setBackgroundResource(R.color.defoliation)
                "Wiring" -> textView.setBackgroundResource(R.color.wiring)
                "Fertilizing" -> textView.setBackgroundResource(R.color.fertilizing)
                "Watering" -> textView.setBackgroundResource(R.color.watering)
                "Propagation" -> textView.setBackgroundResource(R.color.propagation)
                "Diseases" -> textView.setBackgroundResource(R.color.diseases)
                "Specifics" -> textView.setBackgroundResource(R.color.specifics)
                else -> {
                    textView.setBackgroundResource(R.color.white)
                }
            }

            // When task button is clicked, show the alert
            textView.setOnClickListener {

                // If currentTask.shortDescription is empty or XXX, just show Toast
                if (currentTask.shortDescription != "" && currentTask.shortDescription != "xxx") {

                    // Build alert dialog
                    val dialogBuilder = AlertDialog.Builder(textView.context)

                    // Set message of alert dialog
                    dialogBuilder.setMessage(currentTask.shortDescription)
                        // If the dialog is cancelable
                        .setCancelable(false)
                        // Negative button text and action
                        .setNegativeButton("Ok!") { dialog, _ ->
                            dialog.cancel()
                        }

                    // Create dialog box
                    val alert = dialogBuilder.create()
                    // Set title for alert dialog box
                    alert.setTitle("Short Description")
                    // Show alert dialog
                    alert.show()
                }
            }
        }
        return textView
    }

    // Function to set up text view for Task name
    private fun setUpTextViewTaskName(context: Context, textView: TextView, currentTask: Task): TextView {
        // Put this into a function
        textView.text = currentTask.treeSpecies.name
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15F) // text size 15dp
        textView.setPadding(0, 8, 0, 0) // padding 8
        textView.gravity = Gravity.CENTER_HORIZONTAL  // gravity center_horizontal|center_vertical
        // This overwrote the text color
        textView.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium) // textAppearance "?attr/textAppearanceHeadline6"
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.black))
        textView.setBackgroundResource(R.color.repotting)

        // Set text color and background color by task type
        when (currentTask.taskType.name) {
            "Repotting" -> textView.setBackgroundResource(R.color.repotting)
            "Location" -> textView.setBackgroundResource(R.color.location)
            "Pinching" -> textView.setBackgroundResource(R.color.pinching)
            "Pinching (ph1)" -> textView.setBackgroundResource(R.color.pinching)
            "Pinching (ph2)" -> textView.setBackgroundResource(R.color.pinching)
            "Pruning" -> textView.setBackgroundResource(R.color.pruning)
            "Defoliation" -> textView.setBackgroundResource(R.color.defoliation)
            "Defoliation (ph1)" -> textView.setBackgroundResource(R.color.defoliation)
            "Defoliation (ph2)" -> textView.setBackgroundResource(R.color.defoliation)
            "Wiring" -> textView.setBackgroundResource(R.color.wiring)
            "Fertilizing" -> textView.setBackgroundResource(R.color.fertilizing)
            "Watering" -> textView.setBackgroundResource(R.color.watering)
            "Propagation" -> textView.setBackgroundResource(R.color.propagation)
            "Diseases" -> textView.setBackgroundResource(R.color.diseases)
            "Specifics" -> textView.setBackgroundResource(R.color.specifics)
            else -> {
                textView.setBackgroundResource(R.color.white)
            }
        }

        // Define the message and title for the confirmation dialog
        val confirmMessage = "Are you sure you want to delete this task?"
        val confirmTitle = "Confirm Task Deletion"

        // Create a confirmation dialog
        val confirmDialogBuilder = AlertDialog.Builder(context)
        confirmDialogBuilder.setMessage(confirmMessage)
            .setTitle(confirmTitle)
            // Set the action for the "Yes" button
            .setPositiveButton("Yes") { _, _ ->
                sharedBonsaiViewModel.delete(currentTask)
            }
            // Set the action for the "Cancel" button
            .setNegativeButton("Cancel") { _, _ ->
                // Do nothing
            }

        // Build the confirmation dialog
        val confirmDialog = confirmDialogBuilder.create()

        // Set dialog box for task name clicks
        textView.setOnClickListener{
            // build alert dialog
            val dialogBuilderTaskName = AlertDialog.Builder(textView.context)

            // set message of alert dialog
            dialogBuilderTaskName.setMessage(currentTask.longDescription)
                // if the dialog is cancelable
                .setCancelable(false)
                // negative button text and action
                .setNegativeButton("Back") { dialog, _ ->
                    dialog.cancel()
                }
                .setNeutralButton("Delete Task") {_, _ ->
                confirmDialog.show()
                    //sharedBonsaiViewModel.delete(currentTask)
                }

            // create dialog box
            val alert = dialogBuilderTaskName.create()
            // set title for alert dialog box
            alert.setTitle(currentTask.treeSpecies.name + ": " + currentTask.taskType.name)
            // show alert dialog
            alert.show()
        }
        return textView
    }
}