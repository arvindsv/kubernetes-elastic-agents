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

package cd.go.contrib.elasticagent.requests;

import cd.go.contrib.elasticagent.*;
import cd.go.contrib.elasticagent.executors.CreateAgentRequestExecutor;
import cd.go.contrib.elasticagent.model.JobIdentifier;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.fabric8.kubernetes.api.model.EnvVar;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static cd.go.contrib.elasticagent.utils.Util.GSON;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class CreateAgentRequestContext {
    @Expose
    @SerializedName("auto_register_key")
    private String autoRegisterKey;
    @Expose
    @SerializedName("elastic_agent_profile_properties")
    private Map<String, String> properties;
    @Expose
    @SerializedName("environment")
    private String environment;
    @Expose
    @SerializedName("job_identifier")
    private JobIdentifier jobIdentifier;
    @Expose
    @SerializedName("cluster_profile_properties")
    private ClusterProfileProperties clusterProfileProperties;

    private ConsoleLogAppender consoleLogAppender = new ConsoleLogAppender() {
        @Override
        public void accept(String s) {
            // Do nothing.
        }
    };

    public CreateAgentRequestContext() {
    }

    private CreateAgentRequestContext(String autoRegisterKey, Map<String, String> properties, String environment) {
        this.autoRegisterKey = autoRegisterKey;
        this.properties = properties;
        this.environment = environment;
    }

    public CreateAgentRequestContext(String autoRegisterKey, Map<String, String> properties, String environment, JobIdentifier identifier) {
        this(autoRegisterKey, properties, environment);
        this.jobIdentifier = identifier;
    }

    public static CreateAgentRequestContext fromJSON(String json, PluginRequest pluginRequest) {
        CreateAgentRequestContext context = GSON.fromJson(json, CreateAgentRequestContext.class);

        ConsoleLogAppender consoleLogAppender = text -> {
            final String message = String.format("%s %s\n", LocalTime.now().toString(ConsoleLogAppender.MESSAGE_PREFIX_FORMATTER), text);
            pluginRequest.appendToConsoleLog(context.jobIdentifier(), message);
        };

        context.setConsoleLogAppender(consoleLogAppender);
        return context;
    }

    private void setConsoleLogAppender(ConsoleLogAppender consoleLogAppender) {
        this.consoleLogAppender = consoleLogAppender;
    }

    public String autoRegisterKey() {
        return autoRegisterKey;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public String environment() {
        return environment;
    }

    public JobIdentifier jobIdentifier() {
        return jobIdentifier;
    }

    public ClusterProfileProperties clusterProfileProperties() {
        return clusterProfileProperties;
    }

    public RequestExecutor executor(AgentInstances<KubernetesInstance> agentInstances, PluginRequest pluginRequest) {
        return new CreateAgentRequestExecutor(this, agentInstances, pluginRequest);
    }

    public Collection<EnvVar> autoregisterPropertiesAsEnvironmentVars(String elasticAgentId) {
        ArrayList<EnvVar> vars = new ArrayList<>();
        if (isNotBlank(autoRegisterKey)) {
            vars.add(new EnvVar("GO_EA_AUTO_REGISTER_KEY", autoRegisterKey, null));
        }
        if (isNotBlank(environment)) {
            vars.add(new EnvVar("GO_EA_AUTO_REGISTER_ENVIRONMENT", environment, null));
        }
        vars.add(new EnvVar("GO_EA_AUTO_REGISTER_ELASTIC_AGENT_ID", elasticAgentId, null));
        vars.add(new EnvVar("GO_EA_AUTO_REGISTER_ELASTIC_PLUGIN_ID", Constants.PLUGIN_ID, null));
        return vars;
    }

    @Override
    public String toString() {
        return "CreateAgentRequestContext{" +
                "autoRegisterKey='" + autoRegisterKey + '\'' +
                ", properties=" + properties +
                ", environment='" + environment + '\'' +
                ", jobIdentifier=" + jobIdentifier +
                ", clusterProfileProperties=" + clusterProfileProperties +
                '}';
    }

    public void log(String message, Object... args) {
        consoleLogAppender.accept(String.format(message, args));
    }
}
