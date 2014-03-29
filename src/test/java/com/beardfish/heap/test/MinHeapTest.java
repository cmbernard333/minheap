package com.beardfish.heap.test;

import org.junit.Assert;
import org.junit.Test;

import com.beardfish.heap.MinHeap;

public class MinHeapTest {

	@Test
	public void testMinHeapOffer() {
		MinHeap<Integer> minHeap = new MinHeap<Integer>();
		int [] numbers = {100,19,36,17,3,25,1,2,7};
		for(int i = 0; i <numbers.length; i++) {
			minHeap.offer(numbers[i]);
		}
		for(int i = 0; i <numbers.length; i++) {
			Assert.assertTrue(minHeap.contains(numbers[i]));
		}
	}
	
	@Test
	public void testMinHeapRemove() {
		MinHeap<Integer> minHeap = new MinHeap<Integer>();
		int [] numbers = {100,19,36,17,3,25,1,2,7};
		for(int i = 0; i <numbers.length; i++) {
			minHeap.offer(numbers[i]);
		}
	}
}
