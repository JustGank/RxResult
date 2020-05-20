## 一、简介
日常开发中，我们经常要处理，请求响应式的业务。而这种业务往往无法通过一个Activity或者一个模块完成，在组件化类型项目中会变的尤为明显。那么**如何优雅地构建易维护、可复用的 Android 业务流程** 就成为了一个刚需。

### RxResult是一款基于RxJava的编程范式，框架分为两个部分
#### 其中Lib部分主要实现了将原生：

```java
public void startActivityForResult( Intent intent ,int requestCode); 

public void onActivityResult(int requestCode, int resultCode, Intent data);
```
封装为RxJava链式调用的形式,即请求和响应在一起，这样更容易对业务的整体流程进行理解（如：请求注册-》注册成功返回账户）:

```java
 Observable.just(v).compose(RegisterActivity.register(this))
                    .subscribe(user -> {
                        callback.registerCallback(user);
                    });
```


#### 而Demo部分主要展示了，如何基于RxResult对流程性业务进行封装，体现编程范式-如何优雅地构建易维护、可复用的 Android 业务流程。

## 二、如何体现易维护与可复用
在日常的开发中，我们经常面临着一个业务流程由若干个步骤组成，然后将步骤整理输出成Activity。发起流程时，当我们需要这个流程的返回结果，就会使用startActivityForResult()方法进行跳转请求。然后重写onActivityResult()方法等待回调。

例如：新闻类客户端发评论-》点击评论-》查看是否登录（未登录进入登录流程）-》查看是否实名认证（未认证进入认证流程）-》进入评论流程-》评论结束，评论数量+1

这种开发模式在业务流程只有一个页面时是较为好用的，但是往往业务流程会由多个页面组成，而返回结果一般会不断传递，传递到最后一个业务处理页面待处理完毕后进行返回。那么此时如果继续使用startActivityForResult()的原生方式就会变得很繁琐！因为在流程中需要不断传递参数，然后再将这些参数一次返回。另外如果所有的业务流程都在Activity中实现，且Activity中又有对流程的处理有要求（如可以回退），那么再结合管理这个业务流程的Activity栈时就会更加繁琐。 

以上是以常规原生思路解决异步请求业务流程等待返回时面临的问题。

以上的分析可以看出，要是简单的**ActivityA->ActivityB->ActivityA**这种方式使用startActivityForResult()还是首选的。不过多了他就难以胜任了。不过页面载体不仅可以由Activity实现，他也可以由Fragment实现。那么我们就可以让一个Activity内包含多个Fragment,而每个Fragment实现流程的中的一个步骤，然后由Activity管理，结果由Activity返回。此时就将问题回到了“**ActivityA->ActivityB->ActivityA**”这种模式上了。

下面看下，采用一个Activity管理实现业务流程步骤的Fragment是**如何做到易维护与可复用的**。

### 2.1项目结构
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200520104232169.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA0NTE5OTA=,size_16,color_FFFFFF,t_70#pic_center)
从项目结构中我们可以明显的看出，一个业务流程自己有一个包，包下面有一个steps包里面放置实现业务的Fragment,然后就是管理Fragment以及对外开放功能的Activity。

**一个业务流程对应一个Activity,一个Activity管理若干个Fragment。**

### 2.2 Activity实现步骤管理

```java
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public static final int REQUEST_CODE_LOGIN = 10001;

    private LoginByPwdFragment loginByPwdFragment;
    private LoginBySmsFragment loginBySmsFragment;
    private ProcessHelper processHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        processHelper = new ProcessHelper(this, R.id.frame_layout);
        loginByPwdFragment = ProcessHelper.findOrCreateFragment(LoginByPwdFragment.class, this);
        loginBySmsFragment = ProcessHelper.findOrCreateFragment(LoginBySmsFragment.class, this);
        processHelper.push(loginByPwdFragment);
      	registCallback();
    }
	
	protected void registCallback(){
 		loginByPwdFragment.setCallback(new LoginByPwdFragment.Callback() {
            @Override
            public void loginByPwdCallback(Boolean needSmsVerify, User user) {
            }

            @Override
            public void registerCallback(User user) {
            }
        });

        loginBySmsFragment.setCallback(new LoginBySmsFragment.Callback() {
            @Override
            public void LoginBySmsCallback(User user) {
            }
        });
	}

    private static final int REQUEST_LOGIN = 401;

    public static ObservableTransformer<Bundle, Bundle> ensureLogin(final AppCompatActivity activity, int requestCode) {
        return upstream -> {
            upstream.subscribe(inputBundle -> {
                if (LoginInfo.isLogin()) {
                    RXResult.insertActivityResult(activity,
                            new ActivityResult(requestCode,Activity.RESULT_OK,
                                    IntentBuilder.newInstance().putExtra("user", LoginInfo.currentLoginUser).build(),
                                    null, inputBundle )
                    );
                } else {
                    RXResult.startActivityForResult(activity,new Intent(activity, LoginActivity.class),
                            requestCode,inputBundle);
                }
            });

            return RXResult.obtainActivityResult(activity)
                    .filter(result -> result.requestCode == requestCode)
                    .filter(result -> result.resultCode == Activity.RESULT_OK)
                    .map(result ->{
                        Bundle bundle =result.getRequestContextData();
                        return bundle;
                    });
        };
    }

}

```

Activity是实现分为两个部分，首先是onCreate()部分，里面主要实现了业务流程Fragment的初识化，**然后注册对应回调**。

**第二部分是通过ObservableTransformer对判断是否登录业务进行了统一**

 - 如果是已经登录直接返回结果
 - 未登录使用RXResult.startActivityForResult()启动流程，之后由RXResult.obtainActivityResult返回呗观察者， 等待回调。

**第二部分抽象到其他业务可以看成：**

1.业务管理Activity提供对外方法，此方法判断是否需要开启业务流程

 - 不需要开启RXResult.insertActivityResult(）直接返回结果。
 - 需要开启RXResult.startActivityForResult(）开启流程。

2.返回Observable给请求者，待业务完成时回调RxResult.setResult();

### 2.3 Fragment实现步骤

```java
public class LoginByPwdFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        public void loginByPwdCallback(Boolean needSmsVerify, User user);

        public void registerCallback(User user);

    }

}
```
这里面还是两部分，首先对于onCreateView和onViewCreated一般会实现对应步骤的页面和逻辑，这里根据具体业务实现即可不受限制。

**第二部分是对外开放一个Callback,作为和Activity通信的接口。这个接口是“易维护与可复用”的根本。**


### 2.4为什么易维护与可复用
首先对于步骤来说，一个步骤要做的事是固定的，比如说验证码，登录这些都是一个确定的业务流程。对于这些业务流程来说不确定的是将数据返回给谁，以及是否会开启其它流程，然后将其他流程的结果一起返回。那么对于这种变化我们可以将它封装成对外暴露的接口。**另外由于接口在内部，所以每一个业务步骤都是独立的！**

**当也一个业务独立时显然他是高内聚的，这样复用的时候我们需要哪个步骤，直接粘贴这个步骤就可以了！这体现了可复用。而易维护的道理是一样的，某个步骤出问题时，由于步骤独立，我们只需要在该步骤内部查看问题即可。**

最后由于Activity管理者的介入，**高可复用还体现在，我们可以灵活的控制步骤的顺序以及增减顺序**！

另外对于同一项目内，由于启动业务流程的方法在一个Activity中，那么这个Activity和其管理的Fragment也可以看成一个业务流程的整体！此时我们处理切片式的业务是就变成了：

```java
itemCommentClicks.map(index -> BundleBuilder.newInstance().putInt("index", index).build())
                //体现 逻辑 流  是否登录-》是否验证-》开始评论-》评论成功回调
                .compose(LoginActivity.ensureLogin(MainActivity.this, REQUEST_CODE_LOGIN_FOR_COMMENT))
                .compose(AuthActivity.ensureAuth(MainActivity.this))
                .compose(CommentActivity.startComment(MainActivity.this))
                .map(bundle -> bundle.getInt("index"))
                //根据返回值处理业务
                .subscribe(index -> {});
```

**可以看到，即使是流程嵌套的情况下，使用 RxJava 封装的流程依然不会使流程跳转的代码显得十分混乱，
这点十分可贵，因为这意味着今后流程相关代码不会成为项目中难以维护的模块，而是清晰且高内聚的。**


## 三、 如何使用：
### 2.1Fragment管理工具类ProcessHelper
#### 2.1.1 创建Fragment

```java
ProcessHelper processHelper = new ProcessHelper(this, R.id.frame_layout);
LoginByPwdFragment loginByPwdFragment = ProcessHelper.findOrCreateFragment(LoginByPwdFragment.class, this);
```

#### 2.1.2 Fragment跳转

```java
processHelper.push(loginByPwdFragment);

public void push(Fragment fragment)

public void push(Fragment fragment,boolean addToBackStack)

public void push(Fragment fragment, String tag,boolean addToBackStack)
```

 - fragment：需要跳转的fragment。 
 - tag:跳转fragment在FragmentManager中的标签.
 - addToBackStack是否加入到FragmentManager管理栈中。

### 2.2使用 RxResult.startActivityForResult() 进行跳转

```java

public static void startActivityForResult(AppCompatActivity activity, Intent intent, int requestCode)

public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode)

public static void startActivityForResult(AppCompatActivity activity, Intent intent, int requestCode, Bundle requestContextData)

public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode, Bundle requestContextData)

```

**四个参数分别为：上下文，请求体，请求码，请求参数。**


使用示例请求注册：

```java
public static <T> ObservableTransformer<T, User> register(Fragment fragment) {
        return upstream -> {
            upstream.subscribe(it -> {

                RXResult.startActivityForResult(fragment,
                        IntentBuilder.newInstance(fragment.getContext(), RegisterActivity.class).build(),
                        REQUEST_CODE_LOGIN);

            });

            return RXResult.obtainActivityResult(fragment)
                    .filter(result -> result.requestCode == REQUEST_CODE_LOGIN)
                    .filter(result -> result.resultCode == Activity.RESULT_OK)
                    .map(result -> {
                        return (User) result.getData().getSerializableExtra("user");
                    });
        };
    }
```


### 2.3使用RxResult.setResult()/insertActivityResult()进行返回

#### 通过RxResult.setResult()返回结果：
```java
public static void setResult(AppCompatActivity activity, int resultCode) 

public static void setResult(AppCompatActivity activity, int resultCode, Intent resultData) 
```
返回三个参数：上下文，返回码，返回参数（包含请求参数）

使用示例（注册成功返回）：

```java
pwdSetFragment.setCallback(user -> {
            RXResult.setResult(RegisterActivity.this,
                    Activity.RESULT_OK,
                    IntentBuilder.newInstance().putExtra("user", user).build());

            finish();
        });
```

#### 直接返回结果RxResult.insertActivityResult()：

```java
public synchronized static void insertActivityResult(AppCompatActivity activity, ActivityResult activityResult)

public synchronized static void insertActivityResult(Fragment fragment, ActivityResult activityResult)
```

使用示例（如查看登录状态时发现已登录）：

```java
RXResult.insertActivityResult(activity,
                            new ActivityResult(requestCode,
                                    Activity.RESULT_OK,
                                    IntentBuilder.newInstance()
                                            .putExtra("user", LoginInfo.currentLoginUser)
                                            .build(),
                                    null,
                                    inputBundle )
```

### 2.4 链式封装业务流程：

```java
itemCommentClicks
                //Map变化将传递过来的index转换成bundle
                .map(index -> BundleBuilder.newInstance().putInt("index", index).build())
                //体现 逻辑 流  是否登录-》是否验证-》开始评论-》评论成功回调
                .compose(LoginActivity.ensureLogin(MainActivity.this, REQUEST_CODE_LOGIN_FOR_COMMENT))
                .compose(AuthActivity.ensureAuth(MainActivity.this))
                .compose(CommentActivity.startComment(MainActivity.this))
                //Map变化将传递出去的index转换回来
                .map(bundle -> bundle.getInt("index"))
                //根据返回值处理业务
                .subscribe(index -> {
                    MainItemBean itemBean = adapter.getItem(index);
                    itemBean.commentCount++;
                    adapter.notifyItemChanged(index);
                });
```

**注：当需要获取请求时传递的参数时，只需要根据返回的bundle使用请求时的key即可。
如上面例子中，是由一个页面的列表发出，显然返回的时候需要知道是返回给哪个Item。
那么请求时附带参数“index”,请求响应时会把请求的参数带回来，此时解析返回bundle中的''index'即可获取到请求时传递的参数。**

## 四、做了哪些优化
### 3.1 这里给出原作者的帖子链接：
[https://juejin.im/post/5b0a7088f265da0db721cf73](https://juejin.im/post/5b0a7088f265da0db721cf73)

[https://juejin.im/post/5b6ede81f265da0f9c67d1c2](https://juejin.im/post/5b6ede81f265da0f9c67d1c2)

### 3.2 RxResult是在原基础上优化了：
1.由于Fragment，以及FragmentManager的不统一，在原作基础上增加了AndroidX的支持。

2.原作中侵入性较强，对于业务载体Activity需要继承框架中提供的Activity，这样做缺点有两个：

 - 显然对于引入框架的项目如果存在自己的BaseActivity，这个时候需要对BaseActivity做修改。
 
 - **虽然业务流程都在Fragment中，但不代表Activity自身不需要页面（较为致命的缺陷）。**

为了解决以上问题，将父类改为帮助者的方式实现对流程载体Fragment的控制。

问题二的体现可以查看Demo中的注册流程。

3.优化流程控制，增加流程中断时是返回上一级还是整体结束方法。

4.这里将原作翻译成Java版， 原作中Demo使用的是Kotlin开发，这样可以方便使用Java开发的团队阅读以及比较引入成本。


原帖较长，所以为了节省阅读时间，把原文中比较核心的内容摘了出来。

## 五、封装可复用易维护的流程面临哪些挑战：

1. 流程的体验应当流畅
2. 流程需要支持嵌套
3. 流程步骤间数据传递应当简单
4. 流程需要适应 Android 组件生命周期
5. 流程需要可以简单复用
6. 流程页面在完成后需要比较容易销毁
7. 流程进行中回退行为的处理


## 六、解决方案以及缺陷： 
### 解决方案1： 基于原生startActivityForResult：

 1. 写法过于 Dirty，发起请求和处理结果的逻辑被分散在两处，不易维护。 
 2. 页面中如果存在的多个请求，不同流程回调都被杂糅在一个onActivityResult 里，不易维护。
 3.  如果一个流程包含多个页面，代码编写会非常繁琐，显得力不从心。 
 4. 流程步骤间数据共享基于Intent，没有解决 问题(三)。 
 5. 流程页面的自动销毁和流程进行中回退行为存在矛盾，问题(六) 和 问题(七) 没有很好地解决。

### 解决方案2：EventBus 或者其他基于事件总线的解决方案

 总结来说，就是EventBus容易在时间管理上出现混乱，易导致错误。
 
 **住：这里我感觉为了让回调给到需要的观察者而添加字段的做法并不是较大的开销，这中开销其他的解决方案是也是存在的！所以对于没有太多多复杂业务，开发人员较少的情况下我依然认为EventBus是一个可采用的方案。**

### 解决方案3：FLAG_ACTIVITY_CLEAR_TOP 或许是一种方案

 1. 和 startActivityForResult 一样，写法 Dirty，如果流程很多，维护较为不易;
 2.  即使是同一个流程，在同一个页面中也存在复用的情况，不增加新字段无法在 onNewIntent 里面区分;
 3.  问题(三)没有得到解决，onNewIntent 数据传递也是基于 Intent 的,
 4. 也没有用于步骤间共享数据的措施，共享的数据可能需要从头传到尾; 步骤之间有轻微的耦合：每个步骤需要负责启动它的下一个步骤;

### 解决方案4：利用新开辟的 Activity 栈来完成业务流程

 **开辟一个新的任务栈来处理，手机上最近应用列表上，就会多一个App的栏位（多出来的那个代表流程任务栈）**，也就是说用户在做流程的时候如果按 Home 键切换出去，那他想回来的时候，按 最近应用列表，他会看到两个任务，他不知道回哪个。

###  解决方案5：使用 Fragment 框架封装流程（推荐的解决方案，框架也建立在这个方案之上）
#### 优点

 1. 流程步骤间是解耦的，每个步骤职责清晰，只需要完成自己的事并且通知给宿主; 
 2. 回退支持良好，用户体验流畅;
 3.  销毁流程只需要调用Activity 的 finish 方法，非常轻量级; 
 4. 只有一个 Activity 代表这个流程暴露给外部，封装良好而且易于复用;
 5. 流程步骤间数据的共享变得更简单


 
