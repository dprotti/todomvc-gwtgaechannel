/*!! client */
package com.todomvc.client;

/**
 * The V in MVP.
 */
/*! The V in the [MVP Model](http://en.wikipedia.org/wiki/Model-view-presenter). */
public interface View<P extends Presenter<?>> {

    P getPresenter();

}
