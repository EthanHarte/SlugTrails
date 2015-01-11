package slugtrails.slugtrails;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
 
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.mapdemo.R;

public class MyListAdapter extends BaseExpandableListAdapter {
 
 private Context context;
 private ArrayList<Continent> continentList;
 private ArrayList<Continent> originalList;
  
 public MyListAdapter(Context context, ArrayList<Continent> continentList) {
  this.context = context;
  this.continentList = new ArrayList<Continent>();
  this.continentList.addAll(continentList);
  this.originalList = new ArrayList<Continent>();
  this.originalList.addAll(continentList);
 }
  
 @Override
 public Object getChild(int groupPosition, int childPosition) {
  ArrayList<Country> countryList = continentList.get(groupPosition).getCountryList();
  return countryList.get(childPosition);
 }
 
 @Override
 public long getChildId(int groupPosition, int childPosition) {
  return childPosition;
 }
 
 @Override
 public View getChildView(int groupPosition, int childPosition, boolean isLastChild, 
   View view, ViewGroup parent) {
   
  Country country = (Country) getChild(groupPosition, childPosition);
  if (view == null) {
   LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   view = layoutInflater.inflate(R.layout.child_row, null);
  }
   
  TextView code = (TextView) view.findViewById(R.id.code);
  TextView name = (TextView) view.findViewById(R.id.name);
  TextView population = (TextView) view.findViewById(R.id.population);
  code.setText(country.getCode().trim());
     code.setTextColor(Color.WHITE);
  name.setText(country.getName().trim());
     name.setTextColor(Color.WHITE);
  population.setText(NumberFormat.getNumberInstance(Locale.US).format(country.getPopulation()) + " min");
     population.setTextColor(Color.WHITE);
   
  return view;
 }
 
 @Override
 public int getChildrenCount(int groupPosition) {
   
  ArrayList<Country> countryList = continentList.get(groupPosition).getCountryList();
  return countryList.size();
 
 }
 
 @Override
 public Object getGroup(int groupPosition) {
  return continentList.get(groupPosition);
 }
 
 @Override
 public int getGroupCount() {
  return continentList.size();
 }
 
 @Override
 public long getGroupId(int groupPosition) {
  return groupPosition;
 }
 
 @Override
 public View getGroupView(int groupPosition, boolean isLastChild, View view,
   ViewGroup parent) {
   
  Continent continent = (Continent) getGroup(groupPosition);
  if (view == null) {
   LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   view = layoutInflater.inflate(R.layout.group_row, null);
  }
   
  TextView heading = (TextView) view.findViewById(R.id.heading);
  heading.setText(continent.getName().trim());
     heading.setTextColor(Color.WHITE);
   
  return view;
 }
 
 @Override
 public boolean hasStableIds() {
  return true;
 }
 
 @Override
 public boolean isChildSelectable(int groupPosition, int childPosition) {
  return true;
 }
  
 public void filterData(String query){
   
  query = query.toLowerCase();
  continentList.clear();
   
  if(query.isEmpty()){
   continentList.addAll(originalList);
  }
  else {
    
   for(Continent continent: originalList){

    ArrayList<Continent> newList = new ArrayList<Continent>();
     if(continent.getName().toLowerCase().contains(query)){
      continentList.add(continent);
    }
    if(newList.size() > 0){
     Continent nContinent = new Continent(continent.getName(), continent.getCountryList());
     continentList.add(nContinent);
    }
   }
  }
   
  Log.v("MyListAdapter", String.valueOf(continentList.size()));
  notifyDataSetChanged();
   
 }
 
}
