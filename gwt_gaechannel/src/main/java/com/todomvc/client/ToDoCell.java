/*!! client */
package com.todomvc.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.todomvc.client.command.CommandController;
import com.todomvc.shared.command.todo.AddOrRemoveToDoCommand;
import com.todomvc.shared.command.todo.SetCompletedToDoCommand;
import com.todomvc.shared.command.todo.SetTitleToDoCommand;
import com.todomvc.shared.model.ToDo;

/**
 * A custom <a
 * href="http://www.gwtproject.org/doc/latest/DevGuideUiCustomCells.html"
 * >Cell</a> that renders {@link ToDo} instances.
 * 
 * This cell is rendered in both view and edit modes based on user interaction.
 * In edit mode, browser events are handled in order to update the model item
 * state.
 * 
 * @author Colin Eberhardt
 * @author Duilio Protti
 */
/*!
  A custom [Cell](http://www.gwtproject.org/doc/latest/DevGuideUiCustomCells.html) that renders
  `ToDo` instances.
 */
public class ToDoCell extends AbstractCell<ToDo> {

    /**
     * The HTML templates used to render the cell.
     */
    /*!- Helper interface & private fields */
    interface Templates extends SafeHtmlTemplates {

        /**
         * The view-mode template
         */
        @SafeHtmlTemplates.Template("<div class='{2}' data-timestamp='{3}'>" + "{0} "
                + "<label>{1}</label>" + "<button class='destroy'></a>" + "</div>")
        SafeHtml view(SafeHtml checked, SafeHtml task, String done, String timestamp);

        /**
         * A template that renders a checked input
         */
        @SafeHtmlTemplates.Template("<input class='toggle' type='checkbox' checked>")
        SafeHtml inputChecked();

        /**
         * A template that renders an un-checked input
         */
        @SafeHtmlTemplates.Template("<input class='toggle' type='checkbox'>")
        SafeHtml inputClear();

        /**
         * The edit-mode template
         */
        @SafeHtmlTemplates.Template("<div class='listItem editing'><input class='edit' value='{0}' type='text'></div>")
        SafeHtml edit(String task);
    }

    private static Templates templates = GWT.create(Templates.class);

    /**
     * The item that is currently being edited
     */
    private ToDo editingItem = null;

    /**
     * A flag that indicates that we are starting to edit the cell
     */
    private boolean beginningEdit = false;

    /*! Command controller used for executing edit commands on to-do's. */
    private final CommandController commandController;

    private final ToDoPresenter presenter;

    public ToDoCell(ToDoPresenter presenter, CommandController commandController) {
        super("click", "keyup", "blur", "dblclick");
        this.commandController = checkNotNull(commandController);
        this.presenter = checkNotNull(presenter);
    }

    /*! Render the cell in either edit or view mode. */
    @Override
    public void render(Context context, ToDo value, SafeHtmlBuilder sb) {
        if (isEditing(value)) {
            SafeHtml rendered = templates.edit(value.getTitle());
            sb.append(rendered);
        } else {
            SafeHtml rendered = templates.view(value.isCompleted() ? templates.inputChecked()
                    : templates.inputClear(), SafeHtmlUtils.fromString(value.getTitle()), value
                    .isCompleted() ? "listItem view completed" : "listItem view",
                    // NOTE: The addition of a timestamp here is a bit of a HACK! The
                    // problem is that the CellList uses a HasDataPresenter for rendering.
                    // This class caches the more recent rendered contents for each cell, 
                    // skipping a render if it looks like the cell hasn't changed. However,
                    // this fails for editable cells that are able to change the DOM
                    // representation directly. This hack simply ensures that the presenter
                    // always renders the cell.
                    Long.toString(new Date().getTime()));
            sb.append(rendered);
        }
    }

    @Override
    public boolean isEditing(Context context, Element parent, ToDo value) {
        return isEditing(value);
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, ToDo toDo, NativeEvent event,
            ValueUpdater<ToDo> valueUpdater) {

        String type = event.getType();

        if (isEditing(toDo)) {

            // handle keyup events
            if ("keyup".equals(type)) {
                int keyCode = event.getKeyCode();

                // handle enter key to commit the edit
                if (keyCode == KeyCodes.KEY_ENTER) {
                    commitEdit(parent, toDo);
                    endEdit(context, parent, toDo);
                }

                // handle escape key to cancel the edit
                if (keyCode == KeyCodes.KEY_ESCAPE) {
                    endEdit(context, parent, toDo);
                }
            }

            // handle blur event
            if ("blur".equals(type) && !beginningEdit) {
                commitEdit(parent, toDo);
                endEdit(context, parent, toDo);
            }

        } else {

            // handle double clicks to enter edit more
            if ("dblclick".equals(type)) {
                beginEdit(context, parent, toDo);

                beginningEdit = true;
                InputElement input = getInputElement(parent);
                input.focus();
                beginningEdit = false;
            }

            // when not in edit mode - handle click events on the cell
            if ("click".equals(type)) {

                EventTarget eventTarget = event.getEventTarget();
                Element clickedElement = Element.as(eventTarget);
                String tagName = clickedElement.getTagName();

                // check whether the checkbox was clicked
                if (tagName.equals("INPUT")) {

                    // if so, synchronize the model state
                    InputElement input = clickedElement.cast();
                    Boolean completed = input.isChecked();
                    commandController.executeCommand(new SetCompletedToDoCommand(toDo, completed));

                    // update the 'row' style
                    if (input.isChecked()) {
                        getViewRootElement(parent).addClassName("completed");
                    } else {
                        getViewRootElement(parent).removeClassName("completed");
                    }

                } else if (tagName.equals("BUTTON")) {
                    // if the delete anchor was clicked - delete the item
                    presenter.deleteTask(toDo);
                }
            }
        }

    }

    /**
     * Commits the title change to the task. If the new title is empty, deletes the task. 
     */
    /*!
      On edit, execute a
      [SetTitleToDoCommand](${basePath}/java/com/todomvc/shared/command/todo/SetTitleToDoCommand.java.html).
     */
    private void commitEdit(Element parent, ToDo toDo) {
        InputElement input = getInputElement(parent);
        String newTitle = input.getValue().trim();
        if (newTitle.isEmpty()) {
            presenter.deleteTask(toDo);
        } else if (!newTitle.equals(toDo.getTitle())) {
            commandController.executeCommand(new SetTitleToDoCommand(toDo, input.getValue()));
        }
    }

    /**
     * Begins editing the given item, rendering the cell in edit mode
     */
    private void beginEdit(Context context, Element parent, ToDo value) {
        editingItem = value;
        renderCell(context, parent, value);
    }

    /**
     * Ends editing the given item, rendering the cell in view mode
     */
    private void endEdit(Context context, Element parent, ToDo value) {
        editingItem = null;
        renderCell(context, parent, value);
    }

    /**
     * Renders the cell, replacing the contents of the parent with the newly
     * rendered content.
     */
    private void renderCell(Context context, Element parent, ToDo value) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        render(context, value, sb);
        parent.setInnerHTML(sb.toSafeHtml().asString());
    }

    /**
     * Gets whether the given item is being edited.
     */
    private boolean isEditing(ToDo item) {
        return editingItem == item;
    }

    /**
     * Get the input element in edit mode.
     */
    private InputElement getInputElement(Element parent) {
        return parent.getFirstChild().getFirstChild().<InputElement> cast();
    }

    /**
     * Gets the root DIV element of the view mode template.
     */
    private DivElement getViewRootElement(Element parent) {
        return parent.getFirstChild().<DivElement> cast();
    }

}
