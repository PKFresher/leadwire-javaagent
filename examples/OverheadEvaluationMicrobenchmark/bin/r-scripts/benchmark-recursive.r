folder_fn="results-benchmark-recursive-linear"
results_fn=paste("C:\\Users\\jwa\\Projects\\Kieker\\software\\kieker\\trunk\\examples\\OverheadEvaluationMicrobenchmark\\tmp\\",folder_fn,"\\results.csv",sep="")
output_fn=paste("C:\\Users\\jwa\\Projects\\Kieker\\software\\kieker\\trunk\\examples\\OverheadEvaluationMicrobenchmark\\tmp\\",folder_fn,"\\results-bars.pdf",sep="")

configs.threads=1
configs.loop=3
configs.recursion=c(1,2,4,8,16,32,64)
configs.labels=c("No Probe","Inactive Probe","Collecting Data","Writing Data")
configs.count=length(configs.labels)
results.count=2000000
results.skip =1000000

## "[ recursion , config , loop ]"
meanvalues <- array(dim=c(length(configs.recursion),configs.count,configs.loop,2),dimnames=list(configs.recursion,configs.labels,c(1:configs.loop),c("mean","ci95%")))
medianvalues <- array(dim=c(length(configs.recursion),configs.count,configs.loop,3),dimnames=list(configs.recursion,configs.labels,c(1:configs.loop),c("25%","50%","75%")))
resultsBIG <- array(dim=c(length(configs.recursion),configs.count,configs.threads*configs.loop*(results.count-results.skip)),dimnames=list(configs.recursion,configs.labels,c(1:(configs.threads*configs.loop*(results.count-results.skip)))))
for (cr in (1:length(configs.recursion))) {
  for (cc in (1:configs.count)) {
    for (cl in (1:configs.loop)) {
      results_fn_temp=paste(results_fn, "-", cl, "-", cr, "-", cc, ".csv", sep="")
      for (ct in (1:configs.threads)) {
        results=read.csv2(results_fn_temp,nrows=(results.count-results.skip),skip=(ct-1)*results.count+results.skip,quote="",colClasses=c("NULL","integer"),comment.char="",col.names=c("thread_id","duration_nsec"),header=FALSE)
        resultsBIG[cr,cc,c(((cl-1)*configs.threads*(results.count-results.skip)+1):(cl*configs.threads*(results.count-results.skip)))] <- results[["duration_nsec"]]/(1000)
        meanvalues[cr,cc,cl,"mean"] <- mean(results[["duration_nsec"]])/(1000)
        meanvalues[cr,cc,cl,"ci95%"] <- qnorm(0.975)*sd(results[["duration_nsec"]])/1000/sqrt(length(results[["duration_nsec"]]))
        medianvalues[cr,cc,cl,] <- quantile(results[["duration_nsec"]],probs=c(0.25,0.5,0.75))/1000
      }
      rm(results,results_fn_temp)
    }
  }
}

pdf(output_fn, width=8, height=5, paper="special")
plot.new()
plot.window(xlim=c(min(configs.recursion)-0.5,max(configs.recursion)+0.5),ylim=c(500,max(meanvalues[,,,"mean"])))
axis(1,at=configs.recursion)
axis(2)
title(xlab="Recursion Depth (Number of Executions)",ylab="Execution Time (�s)")
for (cr in configs.recursion) {
  printvalues = matrix(nrow=5,ncol=4,dimnames=list(c("mean","ci95%","25%","50%","75%"),c(1:configs.count)))
  for (cc in (1:configs.count)) {
    printvalues["mean",cc]=mean(resultsBIG[(1:length(configs.recursion))[configs.recursion==cr],cc,c(1:2000000)])
    printvalues["ci95%",cc]=qnorm(0.975)*sd(resultsBIG[(1:length(configs.recursion))[configs.recursion==cr],cc,c(1:2000000)])/sqrt(length(resultsBIG[(1:length(configs.recursion))[configs.recursion==cr],cc,c(1:2000000)]))
    printvalues[c("25%","50%","75%"),cc]=quantile(resultsBIG[(1:length(configs.recursion))[configs.recursion==cr],cc,c(1:2000000)],probs=c(0.25,0.5,0.75))
    #printvalues["mean",cc]=mean(meanvalues[cr,cc,,"mean"])
    #printvalues["ci95%",cc]=mean(meanvalues[cr,cc,,"ci95%"])
    #printvalues["50%",cc]=mean(medianvalues[cr,cc,,"25%"])
    #printvalues["50%",cc]=mean(medianvalues[cr,cc,,"50%"])
    #printvalues["75%",cc]=mean(medianvalues[cr,cc,,"75%"])
  }
  #meanvalues
  rect(cr-0.3,printvalues["mean",3],cr+0.5,printvalues["mean",4])
  rect(cr-0.3,printvalues["mean",2],cr+0.5,printvalues["mean",3])
  rect(cr-0.3,printvalues["mean",1],cr+0.5,printvalues["mean",2])
  rect(cr-0.3,0,cr+0.5,printvalues["mean",1])
  for (cc in (1:configs.count)) {
    lines(c(cr+0.41,cr+0.49),c(printvalues["mean",cc]+printvalues["ci95%",cc],printvalues["mean",cc]+printvalues["ci95%",cc]),col="red")
    lines(c(cr+0.45,cr+0.45),c(printvalues["mean",cc]-printvalues["ci95%",cc],printvalues["mean",cc]+printvalues["ci95%",cc]),col="red")
    lines(c(cr+0.41,cr+0.49),c(printvalues["mean",cc]-printvalues["ci95%",cc],printvalues["mean",cc]-printvalues["ci95%",cc]),col="red")
  }
  #median
  rect(cr-0.4,printvalues["50%",3],cr+0.4,printvalues["50%",4],col="white",border="black")
  rect(cr-0.4,printvalues["50%",3],cr+0.4,printvalues["50%",4],angle=45,density=30)
  rect(cr-0.4,printvalues["50%",2],cr+0.4,printvalues["50%",3],col="white",border="black")
  rect(cr-0.4,printvalues["50%",2],cr+0.4,printvalues["50%",3],angle=135,density=20)
  rect(cr-0.4,printvalues["50%",1],cr+0.4,printvalues["50%",2],col="white",border="black")
  rect(cr-0.4,printvalues["50%",1],cr+0.4,printvalues["50%",2],angle=45,density=10)
  rect(cr-0.4,0,cr+0.4,printvalues["50%",1],col="white",border="black")
  rect(cr-0.4,0,cr+0.4,printvalues["50%",1],angle=135,density=5)
  for (cc in (1:configs.count)) {
    lines(c(cr-0.39,cr-0.31),c(printvalues["75%",cc],printvalues["75%",cc]),col="red")
    lines(c(cr-0.35,cr-0.35),c(printvalues["25%",cc],printvalues["75%",cc]),col="red")
    lines(c(cr-0.39,cr-0.31),c(printvalues["25%",cc],printvalues["25%",cc]),col="red")
  }
  for (cc in (2:configs.count)) {
    labeltext=formatC(printvalues["50%",cc]-printvalues["50%",cc-1],format="f",digits=1)
      rect(cr-(strwidth(labeltext)*0.5),printvalues["50%",cc]-strheight(labeltext),cr+(strwidth(labeltext)*0.5),printvalues["50%",cc],col="white",border="black")
      text(cr,printvalues["50%",cc],labels=labeltext,cex=0.75,col="black",pos=1,offset=0.1)
  }
  print(formatC(printvalues,format="f",digits=4,width=8))
}
invisible(dev.off())
