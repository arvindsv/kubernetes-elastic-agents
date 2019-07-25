/*
 * Copyright 2019 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.elasticagent.executors;

import cd.go.contrib.elasticagent.*;
import cd.go.contrib.elasticagent.model.JobIdentifier;
import cd.go.contrib.elasticagent.requests.CreateAgentRequestContext;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class CreateAgentRequestExecutorTest {
    @Test
    public void shouldAskDockerContainersToCreateAnAgent() throws Exception {
        CreateAgentRequestContext context = mock(CreateAgentRequestContext.class);
        AgentInstances<KubernetesInstance> agentInstances = mock(KubernetesAgentInstances.class);
        PluginRequest pluginRequest = mock(PluginRequest.class);

        ClusterProfileProperties clusterProfileProperties = new ClusterProfileProperties();
        when(context.clusterProfileProperties()).thenReturn(clusterProfileProperties);
        when(context.properties()).thenReturn(new HashMap<>());

        new CreateAgentRequestExecutor(context, agentInstances, pluginRequest).execute();

        verify(context).log(contains("Received request to create an elastic agent pod at %s"), any());
        verify(agentInstances).create(context, clusterProfileProperties, pluginRequest);
        verifyNoMoreInteractions(pluginRequest);
    }

    @Test
    public void shouldLogErrorMessageToConsoleIfAgentCreateFails() throws Exception {
        CreateAgentRequestContext context = mock(CreateAgentRequestContext.class);
        AgentInstances<KubernetesInstance> agentInstances = mock(KubernetesAgentInstances.class);
        PluginRequest pluginRequest = mock(PluginRequest.class);

        when(agentInstances.create(any(), any(), any())).thenThrow(new RuntimeException("Ouch!"));

        try {
            new CreateAgentRequestExecutor(context, agentInstances, pluginRequest).execute();
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            // This is expected. Ignore.
        }

        verify(context).log(eq("Failed to create agent pod: %s"), eq("Ouch!"));
    }
}
