package org.dyndns.fzoli.mvc.test.server.model.map;

import org.dyndns.fzoli.mvc.server.model.JSONModel;
import org.dyndns.fzoli.mvc.server.model.annotation.UseModelMap;
import org.dyndns.fzoli.mvc.server.model.map.ModelMap;
import org.dyndns.fzoli.mvc.test.common.key.ModelKeys;
import org.dyndns.fzoli.mvc.test.server.model.MyTestModel;
import org.dyndns.fzoli.mvc.test.server.model.TestModel;

/**
 *
 * @author zoli
 */
@UseModelMap
public final class TestModelMap extends ModelMap<JSONModel> {

    //ha statikussá teszem, akkor van értelme annak, hogy több listenert képes tájékoztatni, valamint ez esetben mindenkinek ugyan az a modelje (ezáltal eseménye is) lesz.
    private static TestModel testModel = new TestModel();
    private TestModel myTestModel = new MyTestModel();

    public TestModelMap() {
        put(ModelKeys.TEST_MODEL, testModel);
        put(ModelKeys.MY_TEST_MODEL, myTestModel);
    }

}