package com.example.todo.ui.home.homefragment;

/**
 * Listen to the state of keyboard {show, hide} and based on the state, do sth.
 */
public interface KeyBoardVisibilityListener {
    void keyboardVisibilityChange(boolean willBeShown);
}
