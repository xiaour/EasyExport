
# EasyExport使用手册1.0.0

## 介绍
EasyExport专为快速导出而开发的组件，整个组件除了接入外使用非常简单，开发人员只需要两行代码即可实现一个离线导出的功能。 而EasyExport是基于EasyExcel和SpringEvent进行开发。只需要使用EasyExcel的开发规则，就可以定义多种Excel的导出了。
在系统中可能需要在多处使用导出，这样我们为了统一导出的入口和处理过程，需要将执行过程简化。没有必要每个为多个导出编写多个类似的导出逻辑，这也是该组件诞生的背景。
组件设计上，保留了很多可以自定义的接口或方法，可以根据需要对执行的初始化参数或过程进行自定义的设置，提供一些比较灵活的可选项配置。
组件应用应当优先选择接入调度平台进行异步导出的方式，因为导出的过程可能消耗一定量的内存；当然组件也支持同步导出的方式，这种方式应当尽量控制好内存的使用，以及控制导出并发数。
## 功能支持
- ✅  @EasyExportSingle 单个Excel导出。
- ✅  AbsBaseEventListener< ExportEvent > 应用内进度事件通知。
- ✅  支持自定义转换器，实现ExcelConverter可以对导出的记录进行格式化等操作。
- ✅  支持自定义上传逻辑，可以使用本地上传的逻辑也可以使用第三方上传的接口。
- ✅  支持不同用户信息及权限，用户可以将用户权限作为初始化参数传入。
- ✅  分页参数自定义，允许使用非标准的分页信息，可以根据数据库的吞吐量定义每批次最大取出数据量（不推荐）。
- ❌  @EasyExportGroup 多个Excel打包导出（实现中）

##接入使用
###1. 接入：依赖引用
```xml
    <dependency>
        <artifactId>csx-bsf-easyexport</artifactId>
        <groupId>com.yh.csx.bsf</groupId>
        <version>1.9.0-SNAPSHOT</version>
    </dependency>  
```


###2. 接入：配置文件
```properties
#启用easyexport
bsf.easyexport.enabled=true
#以下为可选项
#每次获取的最大记录数
bsf.easyexport.fetch.page.size=5000
#是否删除生成的原始文件，一般生产环境为true，方便调试使用
bsf.easyexport.file.delete=true
#文件每页写入记录数，超过限制写入下一个Sheet，默认单sheet页写入150000
bsf.easyexport.file.sheet.size=15000
#按规范，所有项目都用当使用com.yh.csx.business.api.entity.Page 返回数据，但尚未规范的请设置页码和记录数字段
#页码字段
bsf.easyexport.field.page.number=pageIndex
#每页记录数字段
bsf.easyexport.field.page.size=pageSize
```

###3. 接入：入口代码



```java
/**
* 非规范组件的接入Demo
**/
//初始化任务自定义参数
public String export(String name) {
    //这里是模拟前端传入的所有查询参数，前端应当封装参数到ExportParam
    HashMap<String,String> queryParams =  new HashMap<>();
    queryParams.put("name",name);
    //这里是模拟前端需要传入的映射文件Value，实际上每个导出都需要配置不同的映射文件。而映射文件来自于注解@EasyExportSingle
    String classMapping = easyExportProvider.getMappingClass().get(0).getValue();

    ExportParam param = ExportParam.builder().clazzMapping(classMapping).params(queryParams);

    //初始化导出的信息的要素
    ExportContext exportContext = new ExportContext("VTASKID","THIS_IS_TASK_NAME",param);
    exportContext.currentUser(getUserInfo(context.getUserId()), UserInfo.class);
    exportContext.pageMethod("getTotal","getList");
    //初始化导出的信息上下文
    EasyExportBuilder builder = EasyExportBuilder.builder()
            .context(exportContext)
            .modelHandler(() -> transferModel(exportContext));
    //执行导出、上传、完成（删除本地文件）等操作
    String remoteUrl = easyExportProvider
            //执行导出前会全局加锁，如果没有获取到锁，则会持续排队等待，如果任务执行超过30分钟，锁会自动释放。
            .doExport(builder)
            .upload(() -> doUpload(builder.getExportContext().getLocalFile()))
            //必须执行的方法，否则导出不会释放全局锁，下一个任务无法执行。
            .finish();

    return remoteUrl;
}

private void doUpload(String localFile){
    //do something
}

private ExportContext transferModel(ExportContext exportContext){
    //do something
    return exportContext;
}
```

###4. 接入：监听执行进度（非必须）
```java
@Component
public class ExportEventListener extends AbsBaseEventListener<ExportEvent> {

    @Autowired
    private TaskProvider taskProvider;

    @Override
    public void doEvent(ExportEvent event) throws Exception {
        TaskContext taskContext = new TaskContext(Long.parseLong(event.getTaskId()), taskProvider);
        taskContext.setProgress(event.getProcess());
        log.info("----------->监听到当前进度：{}",event.getProcess());
        if(event.success()){
            log.error("----------->任务完成了，可以做点别的事情，比如发个飞书通知一下！");
        }

    }
}

```


###5. 使用：开发者使用
```java
@Service
@EasyExport
public class ExampleService {

    @EasyExportSingle(value = "不重复的任务名称",modelClass = Cat.class)
    public Page<Cat> getList(Cat cat, PageIndex pageIndex){
        //查询列表的方法
        List<Cat> list = new ArrayList<>();
        Page<Cat> page = new Page<>(0,30,list.size(),list);
        return page;
    }
}
/**
 *@ExcelProperty是EasyExcel组件的定义方法，请按照该组件的使用方法配置
 参考文档：https://www.yuque.com/easyexcel/doc/write#06e004ef-17
 **/
@Data
public class Cat {

    @ExcelProperty(value = "昵称")
    private String nickname;

    @ExcelProperty(value = "年龄")
    private Integer age;

    @ExcelProperty(value = "品种")
    private String category;
}

```


### 6. 使用：获取所有导出映射文件
```java

List<ExportMapping> list = easyExportProvider.getMappingClass();
参数说明
ExportParam
ExportParam是对前端提供的入口参数模板，在开发者@EasyExportSingle后，应用启动会将相关映射文件扫描到本地缓存中，通过接口 easyExportProvider.getMappingClass()可以获取到对应的映射文件，classMapping属性需要前端指定需要执行导出的映射文件；params属性是前端需要将页面查询的所有参数进行封装后全部放入params中；pageId属性是用来标识页面来源的，这个可以根据是否需要用到选择是否使用即可。

EasyExportBuilder
1. pageMethod(var1,var2)是用来指定分页信息取值的方法，按照技术规范，我们指定的返回com.yh.csx.business.api.entity.Page<T>则无需调用该方法自定义，否则需要用户传入获取集合和总记录数的方法。
2. currentUser() 是为了保证各系统不同户体系能够正常使用，需要开发者将对应的用户信息传入。
3. registerConverter() 用户则可以通过自定义转换器，可以格式化Excel数据等操作。
4. context()用于构建任务信息的模板，我们需要在本地任务或离线任务设定上下文信息等要素。

EasyExportProvider
     该类的方法顺序应当保持init("必要").modelHandler("如果有必要").doExport("必要").upload("如果有必要").finish("必要");否则会出现错误。
1. init()  初始化导出模板等相关信息
2. modelHandler()可以对导出模板做部分处理，比如改变背景颜色或文字等动态参数，非必须方法。
3. export() 是具体执行导出的关键代码。
4. upload() 考虑到各个系统可能有其他的上传文件的方式，需要将上传的方法在此实现。
5. finish() 会根据在配置文件中bsf.easyexport.file.delete设定的值决定是否删除生成的原始文件。
AbsBaseEventListener<ExportEvent>
继承该类可以用于实现监听导出执行的进度，也可以在不同状态下进行一些用户的异步操作。
@EasyExport
  注解此类表示需要应用快速导出组件。
@EasyExportSingle
和@EasyExport搭配使用，应用于方法级别，需要指定value(导出的文件名称)和modelClass(导出文件所对应的模板类)。在接入使用后，开发者只需要使用@EasyExport和@EasyExportSingle既能完成一个导出功能的开发。

```

##使用约束
1. 记录数：导出的数据应当尽量小，数据量应控制在单个Excel官方规定的1048576行。在本组件内，数据量超过15万行则新开启Sheet页。

2. 组件默认每次查询5000条，可以根据所使用的数据库性能情况自行设定每次查询的数量，建议保持在5k到10k之间，频繁查询数据库可能并不如一次拉取多条性能好。

3. 由于写入Excel会消耗大量的JVM内存，建议接入调度平台进行异步导出。本组件可以作为同步接口，也可以使用调度平台进行异步调度。

4. 在接入的部分禁止使用多线程方式进行执行导出过程，因为导出的执行过程本身都应当是一个队列形式，如果使用多线程可能会产生IO错误或序列化错误的问题。
