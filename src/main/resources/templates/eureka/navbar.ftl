<div id="navcontainer">
<ul id="navlist">
<li class="one"><a href="">Home</a></li>
<li class="three"><a href="lastn">Last 1000 since startup</a></li>
</ul>
</div>
  
  <dt> &nbsp;</dt> 
  <dd><b>DS Replicas: <b>
      <#list replicas as replica>
          <span class="hlist"><a href="${replica.value}">${replica.key}</a></span>
      </#list>
  </dd>
