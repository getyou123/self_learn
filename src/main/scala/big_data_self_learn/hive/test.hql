
create table `call_test` (
    `pone_number` string,
    `createtime` string,   --day 这个是注释的写法
    `call_minute` int
    );

insert into call_test values('18600000000', '2018-12-10 13:00:00', 1);
insert into call_test values('18600000000', '2018-12-11 13:00:00', 6);
insert into call_test values('18600000000', '2018-12-12 13:00:00', 8);
insert into call_test values('18600000000', '2018-12-13 13:00:00', 4);
insert into call_test values('18600000000', '2018-12-14 13:00:00', 7);
insert into call_test values('18600000000', '2018-12-15 13:00:00', 1);
insert into call_test values('18600000000', '2018-12-16 13:00:00', 6);
insert into call_test values('18600000000', '2018-12-17 13:00:00', 8);
insert into call_test values('18600000000', '2018-12-18 13:00:00', 2);
insert into call_test values('18600000000', '2018-12-19 13:00:00', 4);
insert into call_test values('18600000000', '2018-12-20 13:00:00', 7);
insert into call_test values('18600000000', '2018-12-21 13:00:00', 1);
insert into call_test values('18600000000', '2018-12-22 13:00:00', 6);
insert into call_test values('18600000000', '2018-12-23 13:00:00', 8);
insert into call_test values('15600000000', '2018-12-10 13:00:00', 2);
insert into call_test values('15600000000', '2018-12-11 13:00:00', 4);
insert into call_test values('15600000000', '2018-12-12 13:00:00', 7);
insert into call_test values('15600000000', '2018-12-13 13:00:00', 1);
insert into call_test values('15600000000', '2018-12-14 13:00:00', 6);
insert into call_test values('15600000000', '2018-12-15 13:00:00', 8);
insert into call_test values('15600000000', '2018-12-16 13:00:00', 2);
insert into call_test values('15600000000', '2018-12-17 13:00:00', 4);
insert into call_test values('15600000000', '2018-12-18 13:00:00', 7);

create table cust_info(
phone_num string,
cust_name string,
cust_age int);

insert into cust_info values('18600000000', 'xaioming', 13);
insert into cust_info values('15600000000', 'xiaohuang', 6);
