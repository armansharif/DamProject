package com.dam.commons;

public class Routes {

    public final static String ROOT = "";
    public final static String SW = "/swagger**";

    public final static String GET_ticket = ROOT + "/tickets/{id}";
    public final static String GET_admin_tickets = ROOT + "/admin/tickets";
    public final static String GET_tickets = ROOT + "/tickets";

    public final static String GET_tickets_add = ROOT + "/tickets/add";
    public final static String GET_tickets_close = ROOT + "/tickets/close/{id}";
    public final static String GET_tickets_response = ROOT + "/tickets/response/{id}";

    public final static String GET_tickets_category= ROOT + "/tickets/category";
    public final static String GET_notifications = ROOT + "/notifications";
    public final static String POST_notification_read = ROOT + "/notification/read/{id}";
    public final static String Get_home_services = ROOT + "/home/services";
    public final static String Get_home_slider = ROOT + "/home/slider";
    public final static String GET_users_admin = ROOT + "/admin/users";
    public final static String GET_users_by_id = ROOT + "/users/{id}";
    public final static String PUT_profile = ROOT + "/profile";

    public final static String GET_profile = ROOT + "/profile";

    public final static String POST_user_auth_mobile = ROOT + "/auth/mobile";

    public final static String POST_user_auth_email = ROOT + "/auth/email";

    public final static String POST_forget_pass_email = ROOT + "/forgetPass/email";
    public final static String POST_forget_pass_mobile = ROOT + "/forgetPass/mobile";

    public final static String POST_user_verify_email = ROOT + "/verify/email";

    public final static String POST_user_verify_mobile = ROOT + "/verify/mobile";


    public final static String POST_reset_pass_email = ROOT + "/resetPass/mobile";

    public final static String POST_reset_pass_mobile = ROOT + "/resetPass/email";

    public final static String POST_login = ROOT + "/login";
    public final static String POST_admin_login = ROOT + "/admin/login";
    public final static String POST_admin_users = ROOT + "/admin/users";

    public final static String Get_flags = ROOT + "/flag";

    public final static String Get_damdari_dams = ROOT + "/damdari/{damdariId}";
    public final static String Get_damdari = ROOT + "/damdari";
    public final static String Get_dam = ROOT + "/dam/{damId}";
    public final static String Get_dams = ROOT + "/dam";
    public final static String Get_dams_hasProblem_damdariId = ROOT + "/damHasProblem/{damdariId}";
    public final static String Get_dams_hasProblem = ROOT + "/damHasProblem";
    public final static String Get_dam_status = ROOT + "/damStatus/{damId}";
    public final static String Get_dam_status_csv = ROOT + "/damStatus/csv/{damId}";
    public final static String POST_dam_status = ROOT + "/damStatus/{damId}";
    public final static String POST_dam_add = ROOT + "/dam";

    public final static String PUT_dam_flag = ROOT + "/dam/{damId}/flag/{flagId}";

    public final static String Get_dam_historical_flag = ROOT + "/dam/{damId}/historicalFlag/";
    public final static String DELETE_dam_delete = ROOT + "/dam/{id}";

    public final static String PUT_dam_edit = ROOT + "/dam/{id}";

    public final static String Get_dashboard = ROOT + "/dashboard";
    public final static String Get_dashboard_damdariId = ROOT + "/dashboard/{damdariId}";

    public final static String POST_data = ROOT + "/data";
    public final static String POST_imp_date = ROOT + "/impDate/{damId}";
    public final static String GET_imp_date = ROOT + "/impDate/{damId}";

    public final static String POST_damdari_param = ROOT + "/damdari/{damdariId}/param";
    public final static String GET_damdari_param = ROOT + "/damdari/{damdariId}/param";

    public final static String POST_resource = ROOT + "/damdari/resource/{damdariId}";
    public final static String POST_milking = ROOT + "/damdari/milking/{damdariId}";
}
