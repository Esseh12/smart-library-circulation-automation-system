package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class LibraryDatabase {
    private ArrayList<LibraryItem> items;
    private ArrayList<UserAccount> users;
    private Queue<String[]> reservationQueue;
    private Stack<Object[]> undoStack;
    private LibraryItem[] frequentCache;
    private static final int CACHE_SIZE = 5;

    public LibraryDatabase() {
        this.items = new ArrayList<>();
        this.users = new ArrayList<>();
        this.reservationQueue = new LinkedList<>();
        this.undoStack = new Stack<>();
        this.frequentCache = new LibraryItem[CACHE_SIZE];
    }

    public ArrayList<LibraryItem> getItems() { return items; }
    public ArrayList<UserAccount> getUsers() { return users; }
    public Queue<String[]> getReservationQueue() { return reservationQueue; }
    public Stack<Object[]> getUndoStack() { return undoStack; }
    public LibraryItem[] getFrequentCache() { return frequentCache; }

    public void addItem(LibraryItem item) {
        items.add(item);
        undoStack.push(new Object[]{"ADD", item});
        refreshFrequentCache();
    }

    public boolean removeItem(String itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(itemId)) {
                LibraryItem removed = items.remove(i);
                undoStack.push(new Object[]{"DELETE", removed});
                refreshFrequentCache();
                return true;
            }
        }
        return false;
    }

    public Object[] undoLastAction() {
        if (undoStack.isEmpty()) return null;
        Object[] action = undoStack.pop();
        String actionType = (String) action[0];
        LibraryItem item = (LibraryItem) action[1];
        if ("ADD".equals(actionType)) {
            items.removeIf(i -> i.getId().equals(item.getId()));
        } else if ("DELETE".equals(actionType)) {
            items.add(item);
        }
        refreshFrequentCache();
        return action;
    }

    public LibraryItem findById(String id) {
        for (LibraryItem item : items) {
            if (item.getId().equals(id)) return item;
        }
        return null;
    }

    public UserAccount findUserById(String userId) {
        for (UserAccount user : users) {
            if (user.getUserId().equals(userId)) return user;
        }
        return null;
    }

    public void addUser(UserAccount user) {
        users.add(user);
    }

    public boolean removeUser(String userId) {
        return users.removeIf(u -> u.getUserId().equals(userId));
    }

    public void enqueueReservation(String itemId, String userId) {
        reservationQueue.offer(new String[]{itemId, userId});
    }

    public String[] dequeueNextReservation(String itemId) {
        Queue<String[]> temp = new LinkedList<>();
        String[] result = null;
        while (!reservationQueue.isEmpty()) {
            String[] entry = reservationQueue.poll();
            if (result == null && entry[0].equals(itemId)) {
                result = entry;
            } else {
                temp.offer(entry);
            }
        }
        reservationQueue.addAll(temp);
        return result;
    }

    public boolean hasReservation(String itemId) {
        for (String[] entry : reservationQueue) {
            if (entry[0].equals(itemId)) return true;
        }
        return false;
    }

    private void refreshFrequentCache() {
        ArrayList<LibraryItem> sorted = new ArrayList<>(items);
        sorted.sort((a, b) -> b.getBorrowCount() - a.getBorrowCount());
        for (int i = 0; i < CACHE_SIZE; i++) {
            frequentCache[i] = i < sorted.size() ? sorted.get(i) : null;
        }
    }

    public int countByType(String type) {
        return countByTypeRecursive(items, type, 0);
    }

    private int countByTypeRecursive(ArrayList<LibraryItem> list, String type, int index) {
        if (index >= list.size()) return 0;
        int match = list.get(index).getType().equalsIgnoreCase(type) ? 1 : 0;
        return match + countByTypeRecursive(list, type, index + 1);
    }
}
