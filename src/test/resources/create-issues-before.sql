delete from issue_model;

insert into issue_model
value (1, 'user user', sysdate(),'some description of issue 1',null,'some name 1', 'Created',2);

insert into issue_model
    value (2, 'user user', sysdate(),'some description of issue 2',sysdate()+1,'some name 2', 'Closed',2);

insert into issue_model
    value (3, 'user user', sysdate(),'some description of issue 3',sysdate()+1,'some name 3', 'Resolved',2);

insert into issue_model
    value (4, 'user user', sysdate(),'some description of issue 4',null,'some name 4', 'Created',3);

insert into issue_model
    value (5, 'user user', sysdate(),'some description of issue 5',sysdate()+1,'some name 5', 'Closed',3);

insert into issue_model
    value (6, 'user user', sysdate(),'some description of issue 6',sysdate()+1,'some name 6', 'Resolved',3);
