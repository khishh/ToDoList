package com.example.todo.util;

/**
 * Listen to the state of keyboard {show, hide} and based on the state, do sth.
 * HomeCollectionPagerAdapter and HomeFragment implements this interface.
 */
public interface KeyBoardVisibilityListener {
    void keyboardVisibilityChange(boolean willBeShown);
}
