/*!! client */
package com.todomvc.client;

/**
 * The P in MVP.
 */
/*! The P in the [MVP Model](http://en.wikipedia.org/wiki/Model-view-presenter). */
public interface Presenter<V extends View<?>> {

    void bindView(V view);

}
