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
import cd.go.contrib.elasticagent.requests.CreateAgentRequestContext;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import static cd.go.contrib.elasticagent.KubernetesPlugin.LOG;
import static java.text.MessageFormat.format;

public class CreateAgentRequestExecutor implements RequestExecutor {
    private final AgentInstances<KubernetesInstance> agentInstances;
    private final PluginRequest pluginRequest;
    private final CreateAgentRequestContext context;

    public CreateAgentRequestExecutor(CreateAgentRequestContext context, AgentInstances<KubernetesInstance> agentInstances, PluginRequest pluginRequest) {
        this.context = context;
        this.agentInstances = agentInstances;
        this.pluginRequest = pluginRequest;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        LOG.debug(format("[Create Agent] creating elastic agent for profile {0} in cluster {1}", context.properties(), context.clusterProfileProperties()));

        context.log("Received request to create an elastic agent pod at %s", new DateTime().toString("yyyy-MM-dd HH:mm:ss ZZ"));

        agentInstances.create(context, context.clusterProfileProperties(), pluginRequest);
        return new DefaultGoPluginApiResponse(200);
    }

}

