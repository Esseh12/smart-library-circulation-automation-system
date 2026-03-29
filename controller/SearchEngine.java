package controller;

import model.LibraryItem;
import java.util.ArrayList;
import java.util.List;

public class SearchEngine {

    public List<LibraryItem> linearSearch(List<LibraryItem> items, String query, String field) {
        List<LibraryItem> results = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (LibraryItem item : items) {
            String value = getField(item, field).toLowerCase();
            if (value.contains(q)) results.add(item);
        }
        return results;
    }

    public LibraryItem binarySearchByTitle(List<LibraryItem> sortedItems, String title) {
        int low = 0, high = sortedItems.size() - 1;
        String target = title.toLowerCase().trim();
        while (low <= high) {
            int mid = (low + high) / 2;
            String midVal = sortedItems.get(mid).getTitle().toLowerCase();
            int cmp = midVal.compareTo(target);
            if (cmp == 0) return sortedItems.get(mid);
            else if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }

    public LibraryItem recursiveSearchByTitle(List<LibraryItem> items, String title, int index) {
        if (index >= items.size()) return null;
        if (items.get(index).getTitle().equalsIgnoreCase(title.trim())) return items.get(index);
        return recursiveSearchByTitle(items, title, index + 1);
    }

    public List<LibraryItem> recursiveSearchByAuthor(List<LibraryItem> items, String author, int index, List<LibraryItem> acc) {
        if (index >= items.size()) return acc;
        if (items.get(index).getAuthor().toLowerCase().contains(author.toLowerCase().trim())) {
            acc.add(items.get(index));
        }
        return recursiveSearchByAuthor(items, author, index + 1, acc);
    }

    public List<LibraryItem> searchByType(List<LibraryItem> items, String type) {
        List<LibraryItem> results = new ArrayList<>();
        for (LibraryItem item : items) {
            if (item.getType().equalsIgnoreCase(type)) results.add(item);
        }
        return results;
    }

    private String getField(LibraryItem item, String field) {
        switch (field.toLowerCase()) {
            case "title": return item.getTitle();
            case "author": return item.getAuthor();
            case "type": return item.getType();
            default: return item.getTitle() + " " + item.getAuthor();
        }
    }
}
