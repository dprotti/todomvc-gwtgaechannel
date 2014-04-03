package com.todomvc.shared.command;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.todomvc.TestUtil;
import com.todomvc.shared.model.IdentifiableCollection;

public class BulkSetterCommandTest {

    private class Settable extends TestUtil.BasicHasID {

        private boolean wasSet;

        public Settable(String id) {
            super(id);
            wasSet = false;
        }

        public void setValue() {
            wasSet = true;
        }

        public boolean valueWasSet() {
            return wasSet;
        }
    }

    private class TestIdentifiableCollection extends IdentifiableCollection<Settable> {

        private List<Settable> list;

        public TestIdentifiableCollection(String id) {
            super(id);
            list = Lists.newArrayList();
        }

        @Override
        public boolean add(Settable e) {
            return list.add(e);
        }

        @Override
        public Iterator<Settable> iterator() {
            return list.iterator();
        }

        @Override
        public int size() {
            return list.size();
        }
        
    }

    // BulkSetterCommand is abstract, so make it concrete
    private class TestBulkSetterCommand extends BulkSetterCommand<Void, Settable, TestIdentifiableCollection> {

        public TestBulkSetterCommand(TestIdentifiableCollection c) {
            super(c.getId(), null, "testType");
        }

        @Override
        public boolean isEager() {
            return true;
        }

        @Override
        public boolean canExecuteOn(TestIdentifiableCollection target) {
            return true;
        }

        @Override
        protected void setValue(Settable target, Void value) {
            // we don't care 'value', we just mark that this method was invoked on object 'target'
            target.setValue();
        }
  
    }

    private TestBulkSetterCommand command;

    @Test
    public void testExecuteSetValueOnEachElement() {
        Settable o1 = new Settable("1");
        Settable o2 = new Settable("2");
        TestIdentifiableCollection list = new TestIdentifiableCollection("list1");
        list.addAll(Arrays.asList(o1, o2));
        command = new TestBulkSetterCommand(list);

        command.execute(list);

        for (Settable o : list) {
            assertTrue(o.valueWasSet());
        }
    }

}
