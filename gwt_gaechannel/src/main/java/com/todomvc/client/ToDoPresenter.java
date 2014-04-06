/*!! client */
package com.todomvc.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.inject.Inject;

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.todomvc.client.command.CommandController;
import com.todomvc.client.events.ToDoListAddOrRemoveEvent;
import com.todomvc.client.events.ToDoListUpdatedEvent;
import com.todomvc.client.events.ToDoUpdatedEvent;
import com.todomvc.shared.command.todo.AddOrRemoveToDoCommand;
import com.todomvc.shared.command.todo.RemoveCompletedToDoListCommand;
import com.todomvc.shared.command.todo.SetCompletedToDoListCommand;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;
import com.todomvc.shared.service.ToDoServiceAsync;

/**
 * The presenter for the TodoMVC application.
 * This presenter is responsible for the life cycle of the {@link ToDo} instances.
 *
 * @author Colin Eberhardt
 * @author Duilio Protti
 *
 */
/*!
  The presenter for the TodoMVC application.

  Reacts to interactions with its view and updates application state (locally, on the
  server and on all other editing clients) by executing commands through the
  [CommandController](${basePath}/java/com/todomvc/client/command/CommandController.java.html). 
 */
public class ToDoPresenter implements Presenter<ToDoPresenter.Display> {

    /**
     * The interface that a view for this presenter must implement.
     */
    /*! The interface that a view for this presenter must implement. */
    public interface Display extends View<ToDoPresenter> {

        /**
         * Handler for events raised by the view.
         */
        public interface EventHandler {
            /**
             * Invoked when a user adds a new task.
             */
            void addTask();
        
            /**
             * Invoked when a user wishes to clear completed tasks.
             */
            void clearCompletedTasks();
        
            /**
             * Sets the completed state of all tasks to the given state.
             */
            void markAllCompleted(boolean completed);
        }

        void addhandler(Display.EventHandler handler);

        String getTaskText();

        void clearTaskText();

        void setTaskStatistics(int totalTasks, int completedTasks);

        /**
         * Gets the displayed task list.
         *
         * <ul>
         *   <li>Additions and removals to this list will be reflected in the view.</li>
         *   <li>Modifications to individual tasks within the list <em>WILL NOT</em>
         *     be reflected in the view. Use instead <code>List.set(int, Object)</code> for
         *     updating individual {@link ToDo} instances in the view.</li>
         * </ul>
         */
        /*!
          Gets the displayed task list.
         
          - List additions and removals automatically reflects on the view.
          - Modifications to individual tasks within the list **are not**
            reflected in the view. Should use instead `List.set(int, Object)` for
            updating individual ToDo instances in the view.
         */
        List<ToDo> getDisplayedToDos();

        /*! Informs the view of the current routing state (`ACTIVE`, `COMPLETED`, `ALL`). */
        void setRouting(ToDoRouting routing);

        /*! Informs the user that something went wrong. */
        void showError(String message);
    }

    /*! Handler for view events, defers to private presenter methods. */
    private final Display.EventHandler viewEventHandler = new Display.EventHandler() {
        @Override
        public void addTask() {
            ToDoPresenter.this.addTask();
        }

        @Override
        public void clearCompletedTasks() {
            ToDoPresenter.this.clearCompletedTasks();
        }

        @Override
        public void markAllCompleted(boolean completed) {
            ToDoPresenter.this.markAllCompleted(completed);
        }
    };

    private final CommandController commandController;
    private final ToDoServiceAsync toDoService;
    private final EventBus eventBus;
    private final Historian historian;
    private List<ToDo> displayedToDos;
    private ToDoList toDos;
    private Display view;
    private ToDoRouting routing;

    @Inject
    public ToDoPresenter(CommandController commandController, ToDoServiceAsync toDoService, EventBus eventBus,
                Historian historian) {
        this.commandController = checkNotNull(commandController);
        this.toDoService = checkNotNull(toDoService);
        this.eventBus = checkNotNull(eventBus);
        this.historian = checkNotNull(historian);

        String initialToken = historian.getToken();
        routing = parseRoutingToken(initialToken);
    }

    @Override
    public void bindView(final Display view) {
        this.view = checkNotNull(view);
        this.view.addhandler(viewEventHandler);
        this.view.setRouting(routing);
        displayedToDos = view.getDisplayedToDos();
        setupHistoryHandler();
        toDoService.getSingletonList(new AsyncCallback<ToDoList>() {

            @Override
            public void onFailure(Throwable caught) {
                showServerError(caught);
            }

            @Override
            public void onSuccess(ToDoList toDos) {
                initListEditingAndConnectHandlers(toDos);
                updateTaskStatistics();
            }
            
        });
        /*!
          Listen to task changes. Task may be updated through local user interaction with
          its [ToDoCell](${basePath}/java/com/todomvc/client/ToDoCell.java.html)
          or by commands executed from remote clients.
         */
        eventBus.addHandler(ToDoUpdatedEvent.TYPE, new ToDoUpdatedEvent.Handler() {

            @Override
            public void onEvent(ToDoUpdatedEvent event) {
                updateDisplayOfExistingTask(event.getToDo());
                // TODO would be great to update iff 'completed' status have changed
                // and even more: without traversing the complete toDos list
                updateTaskStatistics();
            }

        });
    }

    /*!
     With the given task list:

      - store a reference to it
      - register with the server as an editor of the list
      - listen for changes to the list
    */
    @VisibleForTesting
    void initListEditingAndConnectHandlers(ToDoList list) {
        setToDoList(list);
        commandController.openCommandChannel(list.getId());
        eventBus.addHandler(ToDoListAddOrRemoveEvent.TYPE, new ToDoListAddOrRemoveEvent.Handler() {

            @Override
            public void onEvent(ToDoListAddOrRemoveEvent event) {
                setToDos(event.getToDoList());
                if (event.isAddition()) {
                    maybeDisplayNewTask(event.getAddedElement());
                } else {
                    removeTaskIfDisplayed(event.getRemovedElement());
                }
                updateTaskStatistics();
            }

        });
        eventBus.addHandler(ToDoListUpdatedEvent.TYPE, new ToDoListUpdatedEvent.Handler() {

            @Override
            public void onEvent(ToDoListUpdatedEvent event) {
                setToDos(event.getToDoList());
                updateDisplayedToDoList();
                updateTaskStatistics();
            }

        });
        updateDisplayedToDoList();
    }

    /*! Set up the history change handler, which provides routing. */
    private void setupHistoryHandler() {
        historian.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                String historyToken = event.getValue();
                routing = parseRoutingToken(historyToken);
                view.setRouting(routing);
                updateDisplayedToDoList();
            }
        });
    }

    private ToDoRouting parseRoutingToken(String token ) {
        if (token.equals("/active")) {
            return ToDoRouting.ACTIVE;
        } else if (token.equals("/completed")) {
            return ToDoRouting.COMPLETED;
        } else {
            return ToDoRouting.ALL;
        }
    }

    /*! Updates the tasks rendered in the UI, respecting the current filter. */
    private void updateDisplayedToDoList() {
        displayedToDos.clear();
        for (ToDo task : toDos) {
            if (shouldDisplayTask(task)) {
                displayedToDos.add(task);
            }
        }
    }

    private void maybeDisplayNewTask(ToDo task) {
        if (shouldDisplayTask(task)) {
            displayedToDos.add(task);
        }
    }

    private void updateDisplayOfExistingTask(ToDo task) {
        if (!shouldDisplayTask(task)) {
            if (displayedToDos.contains(task)) {
                displayedToDos.remove(task);
            }
            return;
        }
        if (!displayedToDos.contains(task)) {
            displayedToDos.add(task);
            return;
        }
        // If we are here, the to-do is being displayed with current filter so update its
        // view (re-render its Cell).
        for (int i = 0; i < displayedToDos.size(); i++) {
            if (displayedToDos.get(i).getId().equals(task.getId())) {
                displayedToDos.set(i, task);
                break;
            }
        }
    }

    private boolean shouldDisplayTask(ToDo toDo) {
        return routing.getMatcher().matches(toDo);
    }

    private void updateTaskStatistics() {
        int totalTasks = toDos.size();
        int completedTasks = 0;
        for (ToDo task : toDos) {
            if (task.isCompleted()) {
                completedTasks++;
            }
        }
        view.setTaskStatistics(totalTasks, completedTasks);
    }

    private void markAllCompleted(boolean completed) {
        /*!
          If invocation to 
          [CommandController](${basePath}/java/com/todomvc/client/command/CommandController.java.html)
          succeeds, a `ToDoListUpdatedEvent` fire and our handler updates the view.
         */
        commandController.executeCommand(new SetCompletedToDoListCommand(toDos, completed));
    }

    /*! Adds a new task based on the user input field and fires a `ToDoListAddOrRemoveEvent`. */
    private void addTask() {
        String taskTitle = view.getTaskText().trim();
        if (taskTitle.isEmpty()) {
            return;
        }
        ToDo toDo = new ToDo(null, taskTitle, false);
        view.clearTaskText();
        commandController.executeCommand(new AddOrRemoveToDoCommand(toDos.getId(), toDo, true));
    }

    /**
     * Deletes the given task and fires a {@link ToDoListAddOrRemoveEvent}.
     */
    /*! Deletes the task and fires a `ToDoListAddOrRemoveEvent`. */
    public void deleteTask(ToDo task) {
        commandController.executeCommand(new AddOrRemoveToDoCommand(toDos.getId(), task, false));
    }

    private void removeTaskIfDisplayed(ToDo task) {
        if (displayedToDos.contains(task)) {
            displayedToDos.remove(task);
        }
    }

    /*! Removes all completed tasks and fires `ToDoListUpdatedEvent`. */
    private void clearCompletedTasks() {
        commandController.executeCommand(new RemoveCompletedToDoListCommand(toDos));
    }

    /*!- Helper methods */
    @VisibleForTesting
    void setToDoList(ToDoList toDos) {
        this.toDos = toDos;
    }

    private void setToDos(ToDoList toDos) {
        this.toDos = checkNotNull(toDos);
    }

    private void showServerError(Throwable caught) {
        view.showError("An error occurred while attempting to contact the server<hr>Error was: "
                + caught.getLocalizedMessage());
    }
}