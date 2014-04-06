package com.todomvc.shared.command;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.todomvc.TestUtil;

public class SetterCommandTest {

    private static class Settable extends TestUtil.BasicHasID {
        
        private Integer value;

        public Settable(String id, Integer value) {
            super(id);
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

    }

    private class TestSetterCommand extends SetterCommand<Integer, Settable> {

        public TestSetterCommand(String id, Integer oldValue, Integer newValue) {
            super(id, oldValue, newValue, "testCommandType");
        }

        @Override
        public boolean isEager() {
            return true;
        }

        @Override
        protected Integer getCurrentValue(Settable target) {
            return target.getValue();
        }

        @Override
        protected void setValue(Settable target, Integer value) {
            target.setValue(value);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRefusesToBuildIdempotentCommand() {
        new TestSetterCommand("1", null, null);
    }

    @Test
    public void testExecute() {
        Settable o = new Settable("settable1", 0);
        TestSetterCommand command = new TestSetterCommand("command1", o.getValue(), 1);

        Assert.assertTrue(command.canExecuteOn(o));

        command.execute(o);

        assertEquals(Integer.valueOf(1), o.getValue());
    }

}
