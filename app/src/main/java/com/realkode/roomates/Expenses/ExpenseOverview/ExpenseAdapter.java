package com.realkode.roomates.Expenses.ExpenseOverview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends BaseAdapter {
    private Context context;

    private ArrayList<Item> items = new ArrayList<Item>();
    private ArrayList<Expense> expenses = new ArrayList<Expense>();


    ExpenseAdapter(Context context) {
        this.context = context;
        loadObjects();
    }

    public void reloadElements() {
        if (expenses != null) {
            items.clear();

            ArrayList<Expense> unfinishedElements = new ArrayList<Expense>(expenses);
            ArrayList<Expense> finishedElements = new ArrayList<Expense>();


            // Generating the two sections of the list

            for (Expense expense : unfinishedElements) {
                if (expense.getNotPaidUp().isEmpty()) {
                    finishedElements.add(expense);
                }
            }
            unfinishedElements.removeAll(finishedElements);


            items.add(new SectionItem("Not paid up"));
            for (Expense expense : unfinishedElements) {
                items.add(new EntryItem(expense.getName(), "Created by " + expense.getOwed().getDisplayName(), expense));
            }

            items.add(new SectionItem("Paid up"));
            for (Expense expense : finishedElements) {
                items.add(new EntryItem(expense.getName(), "Created by " + expense.getOwed().getDisplayName(), expense));
            }

            // Called for updating UI
            notifyDataSetChanged();
        }

    }

    public void loadObjects() {
        ParseQuery<Expense> expenseParseQuery = ParseQuery.getQuery(Expense.class);
        expenseParseQuery.include("createdBy");
        expenseParseQuery.include("owed");
        expenseParseQuery.orderByAscending("createdAt");
        expenseParseQuery.whereEqualTo("household", User.getCurrentUser().getActiveHousehold());

        // Setting Cache-policy
        if (expenses.size() == 0) {
            expenseParseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        }
        else {
            expenseParseQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }


        expenseParseQuery.findInBackground(new FindCallback<Expense>() {
            @Override
            public void done(List<Expense> expenseList, ParseException e) {
                if (e == null) {
                    expenses = new ArrayList<Expense>(expenseList);
                    reloadElements();
                }
            }
        });


    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (items != null) {
            Item item = items.get(i);
            if (!item.isSection()) {
                EntryItem entryItem = (EntryItem)item;
                System.out.println(entryItem.expense.getName());
                return entryItem.expense;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Item item = items.get(position);

        if (item != null) {
            if (item.isSection()) {
                SectionItem sectionItem = (SectionItem) item;
                view = View.inflate(context, R.layout.list_element_section, null);
                view.setBackgroundColor(Color.LTGRAY);
                view.setOnClickListener(null);
                view.setOnLongClickListener(null);
                view.setLongClickable(false);
                TextView title = (TextView) view.findViewById(R.id.sectionTitleTextView);

                title.setText(sectionItem.getTitle());
            } else {
                EntryItem entryItem = (EntryItem) item;
                view = View.inflate(context, R.layout.list_expenses_layout, null);
                TextView title = (TextView) view.findViewById(R.id.textViewList);
                TextView subTitle = (TextView) view.findViewById(R.id.textViewSubTitle);

                title.setText(entryItem.title);
                DecimalFormat df = new DecimalFormat(".00");
                double amount = (entryItem.expense.getTotalAmount().doubleValue())/(entryItem.expense.getPaidUp().size() + entryItem.expense.getNotPaidUp().size());
                if (entryItem.expense.getOwed().getObjectId().equals(ParseUser.getCurrentUser().getObjectId()) && entryItem.expense.getNotPaidUp().size() > 0)
                {
                    amount *= entryItem.expense.getNotPaidUp().size();

                    subTitle.setText("You are owed " + df.format(amount) + " for this expense");
                }
                else if (entryItem.expense.getNotPaidUp().size() > 0  && entryItem.expense.getNotPaidUp().contains(ParseUser.getCurrentUser()))
                {
                    subTitle.setText("You owe " + df.format(amount) + " for this expense");
                }
                else if(entryItem.expense.getNotPaidUp().size() > 0)
                {
                    subTitle.setText("You do not owe anything for this");
                }
                else
                {
                    subTitle.setText("");
                }
            }
        }

        return view;
    }

    private interface Item {
        public boolean isSection();
    }

    private class SectionItem implements Item{

        private final String title;

        public SectionItem(String title) {
            this.title = title;
        }

        public String getTitle(){
            return title;
        }

        @Override
        public boolean isSection() {
            return true;
        }
    }

    public class EntryItem implements Item{

        public final String title;
        public final String subtitle;
        public final Expense expense;

        public EntryItem(String title, String subtitle, Expense element) {
            this.title = title;
            this.subtitle = subtitle;
            this.expense = element;
        }

        @Override
        public boolean isSection() {
            return false;
        }

    }
}
