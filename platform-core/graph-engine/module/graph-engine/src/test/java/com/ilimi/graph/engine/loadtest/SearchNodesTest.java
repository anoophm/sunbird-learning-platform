package com.ilimi.graph.engine.loadtest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeTest;

import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.pattern.Patterns;

import com.ilimi.common.dto.Request;
import com.ilimi.graph.common.enums.GraphHeaderParams;
import com.ilimi.graph.dac.enums.GraphDACParams;
import com.ilimi.graph.dac.enums.SystemNodeTypes;
import com.ilimi.graph.dac.model.Filter;
import com.ilimi.graph.dac.model.MetadataCriterion;
import com.ilimi.graph.dac.model.SearchCriteria;
import com.ilimi.graph.dac.util.Neo4jGraphFactory;
import com.ilimi.graph.engine.router.GraphEngineManagers;

public class SearchNodesTest {
    ActorRef reqRouter = null;
    String graphId = "JAVA_CS";
    String SCENARIO_NAME = "SEARCH_NODES";

    private static final Logger logger = LogManager.getLogger("PerformanceTestLogger");

    @BeforeTest
    public void init() throws Exception {
        String logFileName = SCENARIO_NAME + "_" + System.currentTimeMillis();
        System.out.println("Logs are captured in " + logFileName + ".log file.");
        LoggerUtil.config(logFileName);
        reqRouter = TestUtil.initReqRouter();
        searchAllNodes();
    }

    private void searchAllNodes() {
        try {
            Future<Object> searchRes = searchNodes(reqRouter, graphId, -1);
            TestUtil.handleFutureBlock(searchRes, "searchNodes", GraphDACParams.node_id.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test(threadPoolSize = 500, invocationCount = 500)
    public void searchNodesTest() {
        try {
            Neo4jGraphFactory.getGraphDb(graphId);
            int value = (int) Thread.currentThread().getId() % 10;
            Future<Object> searchRes = searchNodes(reqRouter, graphId, value);
            TestUtil.handleFutureBlock(searchRes, "searchNodes", GraphDACParams.node_id.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void searchNodesTest2() {
        try {
            logger.info("Sleep for 10 seconds");
            logger.info("****************************************************************************************************");
            logger.info("");
            Thread.sleep(10000);
            logger.info("");
            logger.info("");
            logger.info("****************************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test(threadPoolSize = 500, invocationCount = 500)
    public void searchNodesTest3() {
        try {
            Neo4jGraphFactory.getGraphDb(graphId);
            int value = (int) Thread.currentThread().getId() % 10;
            Future<Object> searchRes = searchNodes(reqRouter, graphId, value);
            TestUtil.handleFutureBlock(searchRes, "searchNodes", GraphDACParams.node_id.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Future<Object> searchNodes(ActorRef reqRouter2, String graphId, int value) throws Exception {
        Request request = new Request();
        request.getContext().put(GraphHeaderParams.graph_id.name(), graphId);
        request.put(GraphDACParams.search_criteria.name(), getSearchCriteria(value));
        request.getContext().put(GraphHeaderParams.request_id.name(), "REQUEST_" + Thread.currentThread().getId());
        request.getContext().put(GraphHeaderParams.scenario_name.name(), "SEARCH_NODES");
        request.setManagerName(GraphEngineManagers.SEARCH_MANAGER);
        request.setOperation("searchNodes");
        Future<Object> resp = Patterns.ask(reqRouter, request, TestUtil.timeout);
        /*
         * ActorResponse actorResponse = (ActorResponse) Await.result(resp,
         * TestUtil.timeout.duration()); BaseValueObjectList<BaseValueObject>
         * nodes = (BaseValueObjectList<BaseValueObject>)
         * actorResponse.get(GraphDACParams.NODE_LIST.name());
         * System.out.println("No of Nodes:"+nodes.getValueObjectList().size());
         */
        return resp;
    }

    public SearchCriteria getSearchCriteria(int value) {
        SearchCriteria sc = new SearchCriteria();
        sc.setNodeType(SystemNodeTypes.DATA_NODE.name());
        sc.setObjectType("COURSE");
        // sc.add(SearchConditions.eq("META_TYPE", "TYPE_5"));
        // sc.returnField("META_TYPE").returnField("LEVEL").returnField("META_POLICY");
        MetadataCriterion mc = null; // TODO: need to update this.
        switch (value) {
        case -1:
            mc.addFilter(new Filter("LEVEL", 0));
            mc.addFilter(new Filter("META_TYPE", "TYPE_1"));
            mc.addFilter(new Filter("META_POLICY", "POLICY_1"));
            break;
        case 0:
            mc.addFilter(new Filter("LEVEL", 4));
            mc.addFilter(new Filter("META_POLICY", "POLICY_2"));
            break;
        case 1:
            mc.addFilter(new Filter("META_TYPE", "TYPE_2"));
            mc.addFilter(new Filter("META_POLICY", "POLICY_1"));
            break;
        case 2:
            mc.addFilter(new Filter("LEVEL", 2));
            mc.addFilter(new Filter("META_TYPE", "TYPE_4"));
            mc.addFilter(new Filter("META_POLICY", "POLICY_1"));
            break;
        case 3:
            mc.addFilter(new Filter("LEVEL", 9));
            mc.addFilter(new Filter("META_TYPE", "TYPE_5"));
            mc.addFilter(new Filter("META_POLICY", "POLICY_2"));
            break;
        case 4:
            mc.addFilter(new Filter("META_POLICY", "POLICY_1"));
            break;
        case 5:
            mc.addFilter(new Filter("LEVEL", 1));
            mc.addFilter(new Filter("META_TYPE", "TYPE_3"));
            mc.addFilter(new Filter("META_POLICY", "POLICY_1"));
            break;
        case 6:
            mc.addFilter(new Filter("LEVEL", 3));
            mc.addFilter(new Filter("META_POLICY", "POLICY_2"));
            break;
        case 7:
            mc.addFilter(new Filter("META_TYPE", "TYPE_2"));
            break;
        case 8:
            mc.addFilter(new Filter("META_TYPE", "TYPE_4"));
            mc.addFilter(new Filter("META_POLICY", "POLICY_1"));
            break;
        case 9:
            mc.addFilter(new Filter("LEVEL", 7));
            mc.addFilter(new Filter("META_TYPE", "TYPE_5"));
            mc.addFilter(new Filter("META_POLICY", "POLICY_2"));
            break;
        default:
            mc.addFilter(new Filter("LEVEL", 9));
            mc.addFilter(new Filter("META_TYPE", "TYPE_5"));
            mc.addFilter(new Filter("META_POLICY", "POLICY_2"));
            break;
        }
        sc.addMetadata(mc);
        sc.setResultSize(100);;
        return sc;
    }
}
