package com.powerbank.algorithm;

import com.powerbank.model.PowerBank;

import java.util.ArrayList;
import java.util.List;

/**
 * Core Algorithm Implementation for Smart Powerbank Lending System:
 * 1. Binary Search Algorithm - O(log N) logarithmic search for target battery or capacity.
 * 2. MergeSort Algorithm - O(N log N) divide-and-conquer sorting algorithm.
 */
public class FleetSearchAlgorithm {

    /**
     * Binary Search Result containing matched item, index, number of comparison steps,
     * and human-readable diagnostic execution trace.
     */
    public static class SearchResult {
        private final PowerBank matchedUnit;
        private final int index;
        private final int comparisons;
        private final List<String> executionTrace;

        public SearchResult(PowerBank matchedUnit, int index, int comparisons, List<String> executionTrace) {
            this.matchedUnit = matchedUnit;
            this.index = index;
            this.comparisons = comparisons;
            this.executionTrace = executionTrace;
        }

        public PowerBank getMatchedUnit() {
            return matchedUnit;
        }

        public int getIndex() {
            return index;
        }

        public int getComparisons() {
            return comparisons;
        }

        public List<String> getExecutionTrace() {
            return executionTrace;
        }

        public boolean isFound() {
            return matchedUnit != null;
        }

        @Override
        public String toString() {
            return String.format("SearchResult[found=%b, unit=%s, steps=%d]", isFound(), matchedUnit != null ? matchedUnit.getId() : "null", comparisons);
        }
    }

    /**
     * Algorithm 1: Binary Search by Battery Percentage - O(log N)
     * Searches a sorted list of powerbanks for an exact battery level match,
     * or the closest available match with >= target battery.
     *
     * @param sortedList List of powerbanks sorted ascending by battery percentage
     * @param targetBattery Desired battery percentage (0 - 100)
     * @return SearchResult with execution trace and found PowerBank
     */
    public static SearchResult binarySearchByBattery(List<PowerBank> sortedList, int targetBattery) {
        List<String> trace = new ArrayList<>();
        if (sortedList == null || sortedList.isEmpty()) {
            trace.add("Fleet list is empty. Binary search terminated.");
            return new SearchResult(null, -1, 0, trace);
        }

        int low = 0;
        int high = sortedList.size() - 1;
        int step = 0;

        trace.add(String.format("Starting Binary Search for Battery = %d%% across %d units.", targetBattery, sortedList.size()));

        while (low <= high) {
            step++;
            int mid = low + (high - low) / 2;
            PowerBank midPb = sortedList.get(mid);
            int midBattery = midPb.getBatteryLevel();

            trace.add(String.format("Step %d: Range [%d .. %d], Mid Index %d -> %s (Battery: %d%%)",
                    step, low, high, mid, midPb.getId(), midBattery));

            if (midBattery == targetBattery) {
                trace.add(String.format("SUCCESS: Exact match found at index %d for PowerBank %s (%d%%) in %d comparisons!",
                        mid, midPb.getId(), midBattery, step));
                return new SearchResult(midPb, mid, step, trace);
            } else if (midBattery < targetBattery) {
                trace.add(String.format("Target %d%% > Mid %d%% -> Narrowing search to right half [%d .. %d]",
                        targetBattery, midBattery, mid + 1, high));
                low = mid + 1;
            } else {
                trace.add(String.format("Target %d%% < Mid %d%% -> Narrowing search to left half [%d .. %d]",
                        targetBattery, midBattery, low, mid - 1));
                high = mid - 1;
            }
        }

        // If exact match not found, find closest unit with >= target battery
        int closestIndex = low < sortedList.size() ? low : sortedList.size() - 1;
        PowerBank closest = sortedList.get(closestIndex);
        trace.add(String.format("Exact %d%% not found after %d comparisons. Closest optimal unit: %s (Battery: %d%%)",
                targetBattery, step, closest.getId(), closest.getBatteryLevel()));

        return new SearchResult(closest, closestIndex, step, trace);
    }

    /**
     * Algorithm 2: MergeSort - O(N log N)
     * Custom Divide-and-Conquer sorting algorithm to sort powerbanks ascending by battery percentage.
     */
    public static List<PowerBank> mergeSortByBattery(List<PowerBank> list) {
        if (list == null) return new ArrayList<>();
        if (list.size() <= 1) return new ArrayList<>(list);

        int mid = list.size() / 2;
        List<PowerBank> left = mergeSortByBattery(new ArrayList<>(list.subList(0, mid)));
        List<PowerBank> right = mergeSortByBattery(new ArrayList<>(list.subList(mid, list.size())));

        return merge(left, right);
    }

    private static List<PowerBank> merge(List<PowerBank> left, List<PowerBank> right) {
        List<PowerBank> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).getBatteryLevel() <= right.get(j).getBatteryLevel()) {
                merged.add(left.get(i));
                i++;
            } else {
                merged.add(right.get(j));
                j++;
            }
        }

        while (i < left.size()) {
            merged.add(left.get(i));
            i++;
        }
        while (j < right.size()) {
            merged.add(right.get(j));
            j++;
        }

        return merged;
    }
}
