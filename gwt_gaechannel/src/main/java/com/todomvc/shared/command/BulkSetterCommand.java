/*!! shared.command */
package com.todomvc.shared.command;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import com.todomvc.shared.model.IdentifiableCollection;
import com.todomvc.shared.model.ToDoList;

/**
 * Command that sets a value of type {@code V} on every object of type {@code E} contained
 * in an identifiable collection of identifiable objects.
 *
 * @author Duilio Protti
 */
/*!
  Command that sets a value of type `V` on every identifiable object of type `E` contained
  in an identifiable collection.

  Data carried:

  - **id** of target collection
  - **value** to be set
 */
public abstract class BulkSetterCommand<V, E, T extends IdentifiableCollection<? extends E>>
        extends BaseCommand<T> {

    private V value;

    protected BulkSetterCommand() {
    }

    public BulkSetterCommand(String collectionId, @Nullable V value, String commandType) {
        super(checkNotNull(collectionId), checkNotNull(commandType));
        this.value = value;
    }

    @Override
    public void execute(T collection) {
        for (E object : collection) {
            setValue(object, value);
        }
    }

    /*! Can execute on any collection, regardless of the current state of the items. */
    @Override
    public boolean canExecuteOn(T collection) {
        return collection != null;
    }

    protected abstract void setValue(E target,  @Nullable V value);

    protected V getValue() {
        return value;
    }

}
