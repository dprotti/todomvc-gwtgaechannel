/*!! shared.command */
package com.todomvc.shared.command;

import static com.google.common.base.Preconditions.checkNotNull;

import com.todomvc.shared.model.HasID;

/*! Basic command over an identifiable object of type T. */
public abstract class BaseCommand<T extends HasID> implements Command<T> {

    private String objectId;
    private String commandType;

    protected BaseCommand() {
    }

    public BaseCommand(String objectId, String commandType) {
        this.objectId = checkNotNull(objectId);
        this.commandType = checkNotNull(commandType);
    }

    @Override
    public String getTargetId() {
        return objectId;
    }

    @Override
    public String getCommandType() {
        return commandType;
    }
}
