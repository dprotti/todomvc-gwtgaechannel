/*!! shared.command */
package com.todomvc.shared.command;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.todomvc.shared.model.HasID;

/**
 * Command that sets a value of type {@code V} on an object of type {@code T}.
 *
 * @author Duilio Protti
 */
/*!
  Command that sets a value of type `V` on objects of type `T`.

  Data carried:

  - **id** of target object
  - **value** to be set
 */
public abstract class SetterCommand<V, T extends HasID> extends BaseCommand<T> {

    private V oldValue;
    private V newValue;

    protected SetterCommand() {
    }

    public SetterCommand(String id, @Nullable V oldValue, @Nullable V newValue,
            String commandType) {
        super(checkNotNull(id), checkNotNull(commandType));
        if (Objects.equal(newValue, oldValue)) {
            // avoid processing commands that won't change anything  
            throw new IllegalArgumentException(
                    "Command would be idempotent, old and new value are equal to " + newValue);
        }
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Nullable
    protected V getOldValue() {
        return oldValue;
    }

    @Nullable
    protected V getNewValue() {
        return newValue;
    }

    @Nullable
    protected abstract V getCurrentValue(T target);

    protected abstract void setValue(T target,  @Nullable V value);
    
    @Override
    public void execute(T target) {
        checkNotNull(target);
        setValue(target, newValue);
    }

    /*! Can proceed to execute if target object currently has the previous value. */
    @Override
    public boolean canExecuteOn(T target) {
        return Objects.equal(oldValue, getCurrentValue(target));
    }

}
