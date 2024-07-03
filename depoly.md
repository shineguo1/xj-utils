## deploy项目到git仓库
1. github建立仓库`maven_repository`
2. 本地建立目录例如`D:/idea_workspace/git_respository`, 并与github项目关联
3. 打包本项目到github仓库项目
```bash
mvn clean deploy -Dmaven.test.skip  -DaltDeploymentRepository=self-mvn-repo::default::file:D:/idea_workspace/git-repository
```
4. 进入github仓库项目，上传jar包
```bash
cd D:/idea_workspace/git-repository
git push
```

## 参考
[https://www.jianshu.com/p/862ad27fa741](https://www.jianshu.com/p/862ad27fa741)