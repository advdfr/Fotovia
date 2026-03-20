package com.tools.pixart.effect.callBack;

// We removed the Zomato import because it was causing the error.
// Now we just pass the 'int' position of the filter clicked.

public interface FilterPixItemClickListener {
    void onFilterClicked(int position); 
}