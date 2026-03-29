package controller;

import model.LibraryItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortEngine {

    public enum SortField { TITLE, AUTHOR, YEAR }
    public enum SortAlgorithm { SELECTION, INSERTION, MERGE, QUICK }

    public List<LibraryItem> sort(List<LibraryItem> items, SortField field, SortAlgorithm algorithm) {
        List<LibraryItem> list = new ArrayList<>(items);
        Comparator<LibraryItem> comparator = getComparator(field);
        switch (algorithm) {
            case SELECTION: selectionSort(list, comparator); break;
            case INSERTION: insertionSort(list, comparator); break;
            case MERGE:     mergeSort(list, comparator, 0, list.size() - 1); break;
            case QUICK:     quickSort(list, comparator, 0, list.size() - 1); break;
        }
        return list;
    }

    private Comparator<LibraryItem> getComparator(SortField field) {
        switch (field) {
            case AUTHOR: return Comparator.comparing(LibraryItem::getAuthor, String.CASE_INSENSITIVE_ORDER);
            case YEAR:   return Comparator.comparingInt(LibraryItem::getYear);
            default:     return Comparator.comparing(LibraryItem::getTitle, String.CASE_INSENSITIVE_ORDER);
        }
    }

    private void selectionSort(List<LibraryItem> list, Comparator<LibraryItem> cmp) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (cmp.compare(list.get(j), list.get(minIdx)) < 0) minIdx = j;
            }
            LibraryItem temp = list.get(minIdx);
            list.set(minIdx, list.get(i));
            list.set(i, temp);
        }
    }

    private void insertionSort(List<LibraryItem> list, Comparator<LibraryItem> cmp) {
        int n = list.size();
        for (int i = 1; i < n; i++) {
            LibraryItem key = list.get(i);
            int j = i - 1;
            while (j >= 0 && cmp.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    private void mergeSort(List<LibraryItem> list, Comparator<LibraryItem> cmp, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(list, cmp, left, mid);
            mergeSort(list, cmp, mid + 1, right);
            merge(list, cmp, left, mid, right);
        }
    }

    private void merge(List<LibraryItem> list, Comparator<LibraryItem> cmp, int left, int mid, int right) {
        List<LibraryItem> leftList = new ArrayList<>(list.subList(left, mid + 1));
        List<LibraryItem> rightList = new ArrayList<>(list.subList(mid + 1, right + 1));
        int i = 0, j = 0, k = left;
        while (i < leftList.size() && j < rightList.size()) {
            if (cmp.compare(leftList.get(i), rightList.get(j)) <= 0) {
                list.set(k++, leftList.get(i++));
            } else {
                list.set(k++, rightList.get(j++));
            }
        }
        while (i < leftList.size()) list.set(k++, leftList.get(i++));
        while (j < rightList.size()) list.set(k++, rightList.get(j++));
    }

    private void quickSort(List<LibraryItem> list, Comparator<LibraryItem> cmp, int low, int high) {
        if (low < high) {
            int pi = partition(list, cmp, low, high);
            quickSort(list, cmp, low, pi - 1);
            quickSort(list, cmp, pi + 1, high);
        }
    }

    private int partition(List<LibraryItem> list, Comparator<LibraryItem> cmp, int low, int high) {
        LibraryItem pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (cmp.compare(list.get(j), pivot) <= 0) {
                i++;
                LibraryItem temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
        LibraryItem temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        return i + 1;
    }
}
