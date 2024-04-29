package com.example.opsc_part_2_attempt2

import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Date
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_part_2_attempt2.databinding.ActivityTimsheetEntriesListBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

//timesheet list that shows all the entries by the user in the firestore db
class TimsheetEntriesList : AppCompatActivity() {
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityTimsheetEntriesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //this part just makes sure that the content on the screen is offset...
        //...past any system displays like the task bar or anything else
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets}
        //Recyclerview used to display all the entries in the activity
        val recyclerView:RecyclerView = binding.entriesRecyclerView
        //layoutmanager used to arrange the items in the recyclerview in the  activity
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val user = FirebaseAuth.getInstance().currentUser
        if (user!=null)
        {
            //if user is found, user id is extracted
             userID = user.uid
        }
        else{
            //if user not found then user will be returned to login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        //Adapter called using the List of entries extracted by the "getUserEntries" function
        val adapter = MyAdapter(getUserEntries(userID))
        recyclerView.adapter=adapter




    }

    //the click listener for each item
    /*override fun onItemClick(item: TimesheetEntry) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("selectedItem", item)
        startActivity(intent)
    }*/
    //function to extract user's timesheet entries from the firestore database
    private fun getUserEntries(userID: String):List<TimesheetEntry> {
        val data = mutableListOf<TimesheetEntry>()
        val db = FirebaseFirestore.getInstance()
        //linq query to extract all entries made by the current user using their "UserID"
        //UserID was set in the OnCreate function of this activity
        db.collection("timesheetEntries").whereEqualTo("UserID", userID).
        get().addOnSuccessListener {
            documents ->
            for (document in documents)
            {
                //converts all the entries into TimesheetEntry objects
                //and stores them in the "data" list
                val timesheetEntry = document.toObject(TimesheetEntry::class.java)
                data.add(timesheetEntry)

            }
}
        return data
    }
}
//ViewHolder class just to hold references to each view in the list_item layout
class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

val image : ShapeableImageView = itemView.findViewById(R.id.titleImage)
    val name : TextView = itemView.findViewById(R.id.timesheetNameTV)
    val category : TextView = itemView.findViewById(R.id.categoryTV)
    val description : TextView = itemView.findViewById(R.id.timesheetDescTV)
    val date : TextView = itemView.findViewById(R.id.dateTV)

}

//Adapter class to work with the viewss and assign values based on user data
class MyAdapter(private val data : List<TimesheetEntry>):RecyclerView.Adapter<ItemViewHolder>(){
    //creates viewholder objects to hold the views for manipulation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(view)
    }

    //used to bind values to the viewholder's properties
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val dateformat = "dd-mm-yyyy"
        val currentItem = data[position]
        Picasso.get().load(currentItem.Image).into(holder.image)
        holder.name.text = currentItem.Name
        holder.category.text = currentItem.Category
        holder.description.text = currentItem.Description
        currentItem.Date= stringtoDateUsingSimpleDateFormat(holder.date.text.toString(),dateformat)

    }
    //returns the amount of entries in the data list
    override fun getItemCount(): Int {
        return data.size
    }
    //formatter for string to date conversion
    private fun stringtoDateUsingSimpleDateFormat(dateString: String, dateFormat: String): Date {
        val formatter = SimpleDateFormat(dateFormat)
        return formatter.parse(dateString)
    }


}


