package com.example.dillo.breakr2;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.dillo.breakr2.MainActivity.appLabels;
import static com.example.dillo.breakr2.MainActivity.appPackages;
import static com.example.dillo.breakr2.MainActivity.temp;
public class CustomListAdapter extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;

    //to store the images
    private final Drawable[] imageIDarray;

    //to store the names of apps
    private final String[] nameArray;



    public CustomListAdapter(Activity context, String[] nameArrayParam, Drawable[] imageIDArrayParam, String[] timeArrayParam){

        super(context,R.layout.listview_row , nameArrayParam);
        this.context=context;
        this.imageIDarray = imageIDArrayParam;
        this.nameArray = nameArrayParam;
    }
    public View getView(int position, View view, ViewGroup parent)  {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_row, null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = (TextView) rowView.findViewById(R.id.NameTextViewID);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1ID);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(nameArray[position]);

        try{     imageView.setImageDrawable(imageIDarray[position]);
        } catch(Exception e ){
            Log.v("Exception caught","hello");
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }
        return rowView;
    }

}
