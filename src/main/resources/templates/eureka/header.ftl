<div id="header">
    <#if amazonInfo??>
        <h3>EUREKA SERVER (AMI: ${amiId}</h3>
    </#if>
  <h4 id="uptime"><font size="+1" color="red"><b>Environment: ${environment!}</b></font>,
      Data center: ${datacenter!}</h4>
<#if amazonInfo??>
     <h4 id="uptime">Zone: ${availabilityZone}, instance-id: ${instanceId}
</#if>
  <h4 id="uptime">Current time: ${currentTime}, Uptime: ${upTime}</h4>
  <hr id="uptime">Lease expiration enabled: ${registry.leaseExpirationEnabled?c},
        Renews threshold: ${registry.numOfRenewsPerMinThreshold},
        Renews (last min): ${registry.numOfRenewsInLastMin}
<#if isBelowRenewThresold>
    <#if registry.selfPreservationModeEnabled>
        <h4 id="uptime"><font size="+1" color="red"><b>RENEWALS ARE LESSER THAN THE THRESHOLD. THE SELF PRESERVATION MODE IS TURNED OFF.THIS MAY NOT PROTECT INSTANCE EXPIRY IN CASE OF NETWORK/OTHER PROBLEMS.</b></font></h4>
    <#else>
        <h4 id="uptime"><font size="+1" color="red"><b>EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.</b></font></h4>
    </#if>
<#elseif !registry.selfPreservationModeEnabled>
    <h4 id="uptime"><font size="+1" color="red"><b>THE SELF PRESERVATION MODE IS TURNED OFF.THIS MAY NOT PROTECT INSTANCE EXPIRY IN CASE OF NETWORK/OTHER PROBLEMS.</b></font></h4>
</#if>
</h4>
</div>
